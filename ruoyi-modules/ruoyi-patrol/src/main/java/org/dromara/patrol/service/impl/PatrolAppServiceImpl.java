package org.dromara.patrol.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.patrol.domain.PatrolAlert;
import org.dromara.patrol.domain.PatrolArea;
import org.dromara.patrol.domain.PatrolAuditLog;
import org.dromara.patrol.domain.PatrolDevice;
import org.dromara.patrol.domain.PatrolDeviceCommand;
import org.dromara.patrol.domain.PatrolLocationTrack;
import org.dromara.patrol.domain.PatrolMedia;
import org.dromara.patrol.domain.PatrolMessage;
import org.dromara.patrol.domain.PatrolSosEvent;
import org.dromara.patrol.entity.AlertCloseRequestDto;
import org.dromara.patrol.entity.AlertDto;
import org.dromara.patrol.entity.AuthSessionDto;
import org.dromara.patrol.entity.DeviceCommandRequestDto;
import org.dromara.patrol.entity.DeviceStatusDto;
import org.dromara.patrol.entity.GpsLocationDto;
import org.dromara.patrol.entity.HeartbeatAckDto;
import org.dromara.patrol.entity.HeartbeatRequestDto;
import org.dromara.patrol.entity.LoginRequestDto;
import org.dromara.patrol.entity.MediaFileDto;
import org.dromara.patrol.entity.PageEnvelope;
import org.dromara.patrol.entity.PatrolAreaDto;
import org.dromara.patrol.entity.PatrolGeoPointDto;
import org.dromara.patrol.entity.PatrolMessageDto;
import org.dromara.patrol.entity.ScannedDeviceDto;
import org.dromara.patrol.entity.SosEventDto;
import org.dromara.patrol.entity.StreamRelayRequestDto;
import org.dromara.patrol.entity.StreamRelayStateDto;
import org.dromara.patrol.entity.TransferRequestDto;
import org.dromara.patrol.entity.UserProfileDto;
import org.dromara.patrol.mapper.PatrolAlertMapper;
import org.dromara.patrol.mapper.PatrolAreaMapper;
import org.dromara.patrol.mapper.PatrolAuditLogMapper;
import org.dromara.patrol.mapper.PatrolDeviceMapper;
import org.dromara.patrol.mapper.PatrolDeviceCommandMapper;
import org.dromara.patrol.mapper.PatrolLocationTrackMapper;
import org.dromara.patrol.mapper.PatrolMediaMapper;
import org.dromara.patrol.mapper.PatrolMessageMapper;
import org.dromara.patrol.mapper.PatrolSosEventMapper;
import org.dromara.patrol.service.IPatrolAppService;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.vo.SysDeptVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.system.service.ISysDeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 巡检App服务实现
 */
@Service
@RequiredArgsConstructor
public class PatrolAppServiceImpl implements IPatrolAppService {

    private static final String TENANT_ID = TenantConstants.DEFAULT_TENANT_ID;
    private static final String APP_CLIENT_ID = "428a8310cd442757ae699df5d894f051";
    private static final String APP_CLIENT_KEY = "app";
    private static final String DEVICE_ID = "HEADSET_001";

    private final SysUserMapper userMapper;
    private final ISysDeptService deptService;
    private final PatrolDeviceMapper deviceMapper;
    private final PatrolDeviceCommandMapper commandMapper;
    private final PatrolLocationTrackMapper locationTrackMapper;
    private final PatrolAlertMapper alertMapper;
    private final PatrolMediaMapper mediaMapper;
    private final PatrolMessageMapper messageMapper;
    private final PatrolAuditLogMapper auditLogMapper;
    private final PatrolAreaMapper areaMapper;
    private final PatrolSosEventMapper sosEventMapper;

    @Override
    public AuthSessionDto login(LoginRequestDto request) {
        String account = normalizeAccount(request.getAccount());
        SysUserVo user = TenantHelper.dynamic(TENANT_ID, () -> userMapper.selectVoOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, account)));
        if (ObjectUtil.isNull(user) || !SystemConstants.NORMAL.equals(user.getStatus())) {
            throw new ServiceException("用户不存在或已停用");
        }
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new ServiceException("账号或密码错误");
        }

        LoginUser loginUser = buildLoginUser(user);
        loginUser.setClientKey(APP_CLIENT_KEY);
        loginUser.setDeviceType("android");
        SaLoginParameter model = new SaLoginParameter()
            .setDeviceType("android")
            .setTimeout(604800L)
            .setActiveTimeout(1800L)
            .setExtra(LoginHelper.CLIENT_KEY, APP_CLIENT_ID);
        LoginHelper.login(loginUser, model);
        return new AuthSessionDto(StpUtil.getTokenValue(), "refresh-" + StpUtil.getTokenValue(), StpUtil.getTokenTimeout(), "Bearer");
    }

    @Override
    public AuthSessionDto refresh(Map<String, String> request) {
        StpUtil.updateLastActiveToNow();
        return new AuthSessionDto(StpUtil.getTokenValue(), request.getOrDefault("refreshToken", "refresh-" + StpUtil.getTokenValue()), StpUtil.getTokenTimeout(), "Bearer");
    }

    @Override
    public UserProfileDto currentUser() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        return new UserProfileDto(
            String.valueOf(loginUser.getUserId()),
            loginUser.getNickname(),
            loginUser.getUsername(),
            blankToDefault(loginUser.getDeptName(), "第一巡逻支队"),
            "",
            "",
            "福州温泉公园",
            "05:24:12",
            "巡逻组 A-42 | 温泉公园重点巡区",
            "0x4F2A"
        );
    }

    @Override
    public List<ScannedDeviceDto> scanDevices() {
        return TenantHelper.dynamic(TENANT_ID, () -> deviceMapper.selectList().stream()
            .map(item -> new ScannedDeviceDto(item.getDeviceId(), item.getDeviceName(), value(item.getSignalBars(), 0), item.getServiceUuid(), Boolean.TRUE.equals(item.getBonded()), item.getMacAddress(), item.getDeviceType()))
            .toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceStatusDto bindDevice(String deviceId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolDevice device = getOrCreateDevice(deviceId);
            device.setOnline(true);
            device.setCloudConnected(true);
            device.setRecordingStatus("IDLE");
            device.setTalking(false);
            device.setLastHeartbeatTime(new Date());
            deviceMapper.insertOrUpdate(device);
            cacheDevice(device);
            return toDeviceStatus(device);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceStatusDto sendDeviceCommand(String deviceId, DeviceCommandRequestDto request) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolDevice device = getOrCreateDevice(deviceId);
            if ("START_RECORD".equals(request.getCommand())) {
                device.setRecordingStatus("RECORDING");
            } else if ("STOP_RECORD".equals(request.getCommand())) {
                device.setRecordingStatus("IDLE");
            } else if ("START_TALK".equals(request.getCommand())) {
                device.setTalking(true);
            } else if ("STOP_TALK".equals(request.getCommand())) {
                device.setTalking(false);
            }
            device.setOnline(true);
            device.setCloudConnected(true);
            deviceMapper.insertOrUpdate(device);
            saveCommand(deviceId, request, "ACKED", "安卓端指令已同步到平台状态");
            cacheDevice(device);
            return toDeviceStatus(device);
        });
    }

    @Override
    public PageEnvelope<AlertDto> alerts(int page, int pageSize) {
        return TenantHelper.dynamic(TENANT_ID, () -> page(alertMapper.selectList().stream().map(this::toAlertDto).toList(), page, pageSize));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlertDto acknowledgeAlert(String alertId) {
        return TenantHelper.dynamic(TENANT_ID, () -> updateAlert(alertId, "HANDLING", null, null, null));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlertDto closeAlert(String alertId, AlertCloseRequestDto request) {
        return TenantHelper.dynamic(TENANT_ID, () -> updateAlert(alertId, "CLOSED", request.getResult() + "：" + request.getNote(), request.getResult(), request.getOperatorId()));
    }

    @Override
    public PageEnvelope<MediaFileDto> mediaFiles(String side, int page, int pageSize) {
        return TenantHelper.dynamic(TENANT_ID, () -> page(mediaMapper.selectList(new LambdaQueryWrapper<PatrolMedia>().eq(PatrolMedia::getStorageSide, side)).stream().map(this::toMediaDto).toList(), page, pageSize));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MediaFileDto> transferMedia(String fileId, TransferRequestDto request) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolMedia original = getOrCreateMedia(fileId, "DEVICE");
            String side = "PHONE_SANDBOX".equals(request.getTarget()) ? "PHONE" : original.getStorageSide();
            MediaFileDto hashing = withTransfer(original, side, "HASHING", 0.1F, false);
            MediaFileDto uploading = withTransfer(original, side, "UPLOADING", 0.55F, false);
            MediaFileDto verifying = withTransfer(original, side, "VERIFYING", 0.9F, false);
            MediaFileDto done = withTransfer(original, side, "DONE", 1F, true);
            PatrolMedia stored = copyMedia(original, side, "PHONE_SANDBOX".equals(request.getTarget()) ? "IDLE" : "DONE", "PHONE_SANDBOX".equals(request.getTarget()) ? 0F : 1F, true);
            mediaMapper.insertOrUpdate(stored);
            return List.of(hashing, uploading, verifying, done);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMedia(String fileId, String side) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            boolean deleted = mediaMapper.delete(new LambdaQueryWrapper<PatrolMedia>()
                .eq(PatrolMedia::getFileId, fileId)
                .eq(side != null && !side.isBlank(), PatrolMedia::getStorageSide, side)) > 0;
            saveAudit("MEDIA", "App删除媒体文件", fileId, deleted ? "SUCCESS" : "FAILED");
            return deleted;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean verifyMedia(String fileId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            List<PatrolMedia> files = mediaMapper.selectList(new LambdaQueryWrapper<PatrolMedia>().eq(PatrolMedia::getFileId, fileId));
            files.forEach(file -> {
                file.setSha256Verified(true);
                mediaMapper.updateById(file);
            });
            saveAudit("MEDIA", "App校验媒体文件", fileId, files.isEmpty() ? "FAILED" : "SUCCESS");
            return !files.isEmpty();
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HeartbeatAckDto heartbeat(HeartbeatRequestDto request) {
        TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolDevice device = getOrCreateDevice(request.getDeviceId());
            device.setOnline(request.isOnline());
            device.setBatteryPercent(request.getBatteryPercent());
            device.setSignalBars(request.getSignalBars());
            device.setRecordingStatus(request.getRecordingStatus());
            device.setCloudConnected(true);
            device.setLastHeartbeatTime(new Date());
            if (request.getLatitude() != null && request.getLongitude() != null) {
                device.setLatitude(request.getLatitude());
                device.setLongitude(request.getLongitude());
                device.setAddress(blankToDefault(request.getAddress(), device.getAddress()));
                saveLocationTrack(device, request);
            }
            deviceMapper.insertOrUpdate(device);
            cacheDevice(device);
        });
        return new HeartbeatAckDto(request.isOnline(), Instant.now().getEpochSecond(), 15);
    }

    @Override
    public PageEnvelope<PatrolMessageDto> messages(String targetId, int page, int pageSize) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            String safeTargetId = blankToDefault(targetId, LoginHelper.getUsername());
            List<PatrolMessageDto> items = messageMapper.selectList(new LambdaQueryWrapper<PatrolMessage>()
                    .orderByDesc(PatrolMessage::getSentAt))
                .stream()
                .filter(item -> isMessageVisible(item, safeTargetId))
                .map(this::toMessageDto)
                .toList();
            return page(items, page, pageSize);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PatrolMessageDto readMessage(String messageId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolMessage message = messageMapper.selectById(messageId);
            if (message == null) {
                throw new ServiceException("消息不存在");
            }
            int total = Math.max(value(message.getTotalCount(), 0), 1);
            int read = Math.min(value(message.getReadCount(), 0) + 1, total);
            message.setReadCount(read);
            if (read >= total) {
                message.setStatus("READ");
            }
            messageMapper.updateById(message);
            saveAudit("MESSAGE", "App读取指挥消息", messageId, "SUCCESS");
            return toMessageDto(message);
        });
    }

    @Override
    public StreamRelayStateDto startStream(StreamRelayRequestDto request) {
        StreamRelayStateDto state = new StreamRelayStateDto("FAILED", null, null);
        RedisUtils.setCacheObject("patrol:stream:" + request.getDeviceId(), state, Duration.ofMinutes(30));
        return state;
    }

    @Override
    public StreamRelayStateDto stopStream() {
        return new StreamRelayStateDto("IDLE", null, null);
    }

    @Override
    public PatrolAreaDto currentPatrolArea() {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolArea area = areaMapper.selectById("AREA-FZ-WQ-001");
            List<PatrolGeoPointDto> boundary = JsonUtils.parseArray(area.getBoundaryJson(), PatrolGeoPointDto.class);
            List<PatrolGeoPointDto> route = JsonUtils.parseArray(area.getRouteJson(), PatrolGeoPointDto.class);
            return new PatrolAreaDto(area.getAreaId(), area.getAreaName(), area.getTeamId(), area.getTeamName(), boundary, route);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SosEventDto activateSos(GpsLocationDto location) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolSosEvent event = new PatrolSosEvent();
            event.setTenantId(TENANT_ID);
            event.setSosId("SOS-" + Instant.now().toEpochMilli());
            event.setPhase("ACTIVE");
            event.setMessage("紧急上报已激活");
            event.setLatitude(location.getLatitude());
            event.setLongitude(location.getLongitude());
            event.setAccuracyMeters(location.getAccuracyMeters());
            event.setAddress(location.getAddress());
            event.setRecordingAudio(true);
            event.setBackupEtaMinutes(4);
            event.setDelFlag("0");
            sosEventMapper.insert(event);
            return toSosDto(event);
        });
    }

    @Override
    public SosEventDto cancelSos() {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolSosEvent event = sosEventMapper.selectList(new LambdaQueryWrapper<PatrolSosEvent>()
                    .eq(PatrolSosEvent::getPhase, "ACTIVE")
                    .orderByDesc(PatrolSosEvent::getCreateTime))
                .stream()
                .findFirst()
                .orElse(null);
            if (event == null) {
                return new SosEventDto("SOS-CANCEL", "CANCELLED", "紧急上报已取消", null, false, null);
            }
            event.setPhase("CANCELLED");
            event.setMessage("紧急上报已取消");
            event.setRecordingAudio(false);
            sosEventMapper.updateById(event);
            saveAudit("SOS", "App取消SOS求助", event.getSosId(), "SUCCESS");
            return toSosDto(event);
        });
    }

    private LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(user.getTenantId());
        loginUser.setUserId(user.getUserId());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        loginUser.setUserType(user.getUserType());
        if (ObjectUtil.isNotNull(user.getDeptId())) {
            SysDeptVo dept = deptService.selectDeptById(user.getDeptId());
            if (ObjectUtil.isNotNull(dept)) {
                loginUser.setDeptName(dept.getDeptName());
                loginUser.setDeptCategory(dept.getDeptCategory());
            }
        }
        return loginUser;
    }

    private PatrolDevice getOrCreateDevice(String deviceId) {
        PatrolDevice device = deviceMapper.selectById(deviceId);
        if (device != null) {
            return device;
        }
        device = new PatrolDevice();
        device.setTenantId(TENANT_ID);
        device.setDeviceId(deviceId);
        device.setDeviceName("ForceLink-H1");
        device.setDeviceType("HEADSET");
        device.setServiceUuid("0000-pl2-ble-control");
        device.setMacAddress("2C:4A:91:3F:8B:02");
        device.setBonded(true);
        device.setOnline(true);
        device.setBatteryPercent(88);
        device.setSignalBars(4);
        device.setOnlineDuration("02:45:12");
        device.setStorageUsedGb(42.5F);
        device.setStorageTotalGb(128F);
        device.setFirmwareVersion("v1.2.4");
        device.setRecordingStatus("IDLE");
        device.setTalking(false);
        device.setCloudConnected(true);
        device.setAddress("福州温泉公园");
        device.setDelFlag("0");
        return device;
    }

    private PatrolAlert getOrCreateAlert(String alertId) {
        PatrolAlert alert = alertMapper.selectById(alertId);
        if (alert != null) {
            return alert;
        }
        alert = new PatrolAlert();
        alert.setTenantId(TENANT_ID);
        alert.setAlertId(alertId);
        alert.setTitle("预警事件");
        alert.setLevel("INFO");
        alert.setStatus("PENDING");
        alert.setOccurredAt("15:00");
        alert.setLocationText("未知位置");
        alert.setSource(DEVICE_ID);
        alert.setDescription("事件状态已更新。");
        alert.setConfidence("0%");
        alert.setDelFlag("0");
        return alert;
    }

    private AlertDto updateAlert(String alertId, String status, String description, String result, String operatorId) {
        PatrolAlert alert = getOrCreateAlert(alertId);
        alert.setStatus(status);
        if (description != null) {
            alert.setDescription(description);
            alert.setCloseNote(description);
        }
        alert.setCloseResult(result);
        alert.setOperatorId(operatorId);
        alertMapper.insertOrUpdate(alert);
        return toAlertDto(alert);
    }

    private PatrolMedia getOrCreateMedia(String fileId, String preferredSide) {
        PatrolMedia media = mediaMapper.selectOne(new LambdaQueryWrapper<PatrolMedia>().eq(PatrolMedia::getFileId, fileId).eq(PatrolMedia::getStorageSide, preferredSide).last("limit 1"));
        if (media != null) {
            return media;
        }
        media = mediaMapper.selectOne(new LambdaQueryWrapper<PatrolMedia>().eq(PatrolMedia::getFileId, fileId).last("limit 1"));
        if (media != null) {
            return media;
        }
        media = new PatrolMedia();
        media.setTenantId(TENANT_ID);
        media.setFileId(fileId);
        media.setFileName(fileId);
        media.setMediaType("VIDEO");
        media.setCapturedAt("15:30:00");
        media.setSizeText("0 MB");
        media.setSha256Verified(false);
        media.setStorageSide(preferredSide);
        media.setTransferStatus("IDLE");
        media.setProgress(0F);
        media.setBucketName("patrol-media");
        media.setObjectKey(preferredSide.toLowerCase() + "/" + fileId);
        media.setDelFlag("0");
        return media;
    }

    private PatrolMedia copyMedia(PatrolMedia original, String side, String status, float progress, boolean verified) {
        PatrolMedia media = mediaMapper.selectOne(new LambdaQueryWrapper<PatrolMedia>().eq(PatrolMedia::getFileId, original.getFileId()).eq(PatrolMedia::getStorageSide, side).last("limit 1"));
        if (media == null) {
            media = new PatrolMedia();
            media.setTenantId(TENANT_ID);
            media.setFileId(original.getFileId());
        }
        media.setFileName(original.getFileName());
        media.setMediaType(original.getMediaType());
        media.setCapturedAt(original.getCapturedAt());
        media.setSizeText(original.getSizeText());
        media.setDurationText(original.getDurationText());
        media.setSha256Verified(verified || Boolean.TRUE.equals(original.getSha256Verified()));
        media.setStorageSide(side);
        media.setTransferStatus(status);
        media.setProgress(progress);
        media.setContentUri(original.getContentUri());
        media.setOssId(original.getOssId());
        media.setBucketName("PHONE".equals(side) ? "patrol-evidence" : blankToDefault(original.getBucketName(), "patrol-media"));
        media.setObjectKey(side.toLowerCase() + "/" + original.getFileId());
        media.setSha256(original.getSha256());
        media.setDelFlag("0");
        return media;
    }

    private MediaFileDto withTransfer(PatrolMedia original, String side, String status, float progress, boolean verified) {
        PatrolMedia media = copyMedia(original, side, status, progress, verified);
        return toMediaDto(media);
    }

    private void cacheDevice(PatrolDevice device) {
        RedisUtils.setCacheObject("patrol:device:" + device.getDeviceId(), toDeviceStatus(device), Duration.ofMinutes(5));
    }

    private void saveCommand(String deviceId, DeviceCommandRequestDto request, String status, String resultMessage) {
        PatrolDeviceCommand command = new PatrolDeviceCommand();
        command.setTenantId(TENANT_ID);
        command.setCommandId("CMD-" + UUID.randomUUID());
        command.setDeviceId(deviceId);
        command.setCommand(request.getCommand());
        command.setOperatorId(blankToDefault(request.getOperatorId(), LoginHelper.getUsername()));
        command.setRequestId(request.getRequestId());
        command.setStatus(status);
        command.setResultMessage(resultMessage);
        command.setSentAt(new Date());
        command.setAckAt(new Date());
        command.setDelFlag("0");
        commandMapper.insert(command);
    }

    private void saveLocationTrack(PatrolDevice device, HeartbeatRequestDto request) {
        PatrolLocationTrack track = new PatrolLocationTrack();
        track.setTenantId(TENANT_ID);
        track.setTrackId("TRK-" + UUID.randomUUID());
        track.setBadgeNo("POLICE_9527");
        track.setOfficerName("张警官");
        track.setDeviceId(device.getDeviceId());
        track.setLatitude(request.getLatitude());
        track.setLongitude(request.getLongitude());
        track.setAccuracyMeters(request.getAccuracyMeters());
        track.setAddress(blankToDefault(request.getAddress(), device.getAddress()));
        track.setReportedAt(new Date());
        track.setDelFlag("0");
        locationTrackMapper.insert(track);
    }

    private void saveAudit(String logType, String action, String resource, String result) {
        PatrolAuditLog log = new PatrolAuditLog();
        log.setTenantId(TENANT_ID);
        log.setLogId("AUD-" + UUID.randomUUID());
        log.setLogType(logType);
        log.setOperatorName(blankToDefault(LoginHelper.getUsername(), "app"));
        log.setAction(action);
        log.setResource(resource);
        log.setResult(result);
        log.setIpAddress("127.0.0.1");
        log.setTraceId(UUID.randomUUID().toString());
        log.setOccurredAt(new Date());
        log.setDelFlag("0");
        auditLogMapper.insert(log);
    }

    private boolean isMessageVisible(PatrolMessage message, String targetId) {
        if ("ORG".equals(message.getTargetType())) {
            return true;
        }
        if ("DEVICE".equals(message.getTargetType())) {
            return DEVICE_ID.equals(message.getTargetId());
        }
        return targetId.equals(message.getTargetId());
    }

    private DeviceStatusDto toDeviceStatus(PatrolDevice device) {
        return new DeviceStatusDto(device.getDeviceId(), device.getDeviceName(), Boolean.TRUE.equals(device.getOnline()), value(device.getBatteryPercent(), 0), value(device.getSignalBars(), 0), blankToDefault(device.getOnlineDuration(), "00:00:00"), value(device.getStorageUsedGb(), 0F), value(device.getStorageTotalGb(), 0F), blankToDefault(device.getFirmwareVersion(), ""), blankToDefault(device.getRecordingStatus(), "IDLE"), Boolean.TRUE.equals(device.getTalking()), Boolean.TRUE.equals(device.getCloudConnected()));
    }

    private AlertDto toAlertDto(PatrolAlert alert) {
        return new AlertDto(alert.getAlertId(), alert.getTitle(), alert.getLevel(), alert.getStatus(), alert.getOccurredAt(), alert.getLocationText(), alert.getSource(), alert.getDescription(), alert.getConfidence());
    }

    private MediaFileDto toMediaDto(PatrolMedia media) {
        return new MediaFileDto(media.getFileId(), media.getFileName(), media.getMediaType(), media.getCapturedAt(), media.getSizeText(), media.getDurationText(), Boolean.TRUE.equals(media.getSha256Verified()), media.getStorageSide(), media.getTransferStatus(), value(media.getProgress(), 0F), media.getContentUri());
    }

    private SosEventDto toSosDto(PatrolSosEvent event) {
        GpsLocationDto location = event.getLatitude() == null ? null : new GpsLocationDto(event.getLatitude(), event.getLongitude(), value(event.getAccuracyMeters(), 0F), event.getAddress());
        return new SosEventDto(event.getSosId(), event.getPhase(), event.getMessage(), location, Boolean.TRUE.equals(event.getRecordingAudio()), event.getBackupEtaMinutes());
    }

    private PatrolMessageDto toMessageDto(PatrolMessage message) {
        return new PatrolMessageDto(
            message.getMessageId(),
            message.getTitle(),
            message.getContent(),
            message.getTargetType(),
            message.getStatus(),
            message.getSentAt() == null ? "" : message.getSentAt().toString()
        );
    }

    private <T> PageEnvelope<T> page(List<T> items, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(pageSize, 1);
        int from = Math.min((safePage - 1) * safePageSize, items.size());
        int to = Math.min(from + safePageSize, items.size());
        return new PageEnvelope<>(items.subList(from, to), safePage, safePageSize, items.size(), to < items.size());
    }

    private String normalizeAccount(String account) {
        return account == null || account.isBlank() ? "POLICE_9527" : account.trim();
    }

    private String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private int value(Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }

    private float value(Float value, float defaultValue) {
        return value == null ? defaultValue : value;
    }
}
