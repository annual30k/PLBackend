package org.dromara.patrol.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.oss.factory.OssFactory;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.patrol.domain.PatrolAlert;
import org.dromara.patrol.domain.PatrolAlertAttachment;
import org.dromara.patrol.domain.PatrolAlertDisposition;
import org.dromara.patrol.domain.PatrolAppVersion;
import org.dromara.patrol.domain.PatrolArea;
import org.dromara.patrol.domain.PatrolAuditLog;
import org.dromara.patrol.domain.PatrolControlPerson;
import org.dromara.patrol.domain.PatrolDevice;
import org.dromara.patrol.domain.PatrolDeviceBinding;
import org.dromara.patrol.domain.PatrolDeviceCommand;
import org.dromara.patrol.domain.PatrolDeviceConfig;
import org.dromara.patrol.domain.PatrolDeviceEvent;
import org.dromara.patrol.domain.PatrolLocationTrack;
import org.dromara.patrol.domain.PatrolMedia;
import org.dromara.patrol.domain.PatrolMediaUploadTask;
import org.dromara.patrol.domain.PatrolMessage;
import org.dromara.patrol.domain.PatrolMessageReceipt;
import org.dromara.patrol.domain.PatrolSosEvent;
import org.dromara.patrol.entity.AlertCloseRequestDto;
import org.dromara.patrol.entity.AlertDto;
import org.dromara.patrol.entity.AuthSessionDto;
import org.dromara.patrol.entity.CerebellumFaceAlertRequestDto;
import org.dromara.patrol.entity.DeviceAdvancedSettingsDto;
import org.dromara.patrol.entity.DeviceCapabilitiesDto;
import org.dromara.patrol.entity.DeviceCommandRequestDto;
import org.dromara.patrol.entity.DeviceControlResultDto;
import org.dromara.patrol.entity.DeviceStatusDto;
import org.dromara.patrol.entity.DeviceWifiStateDto;
import org.dromara.patrol.entity.FaceLibraryAckRequestDto;
import org.dromara.patrol.entity.FaceLibraryPackageDto;
import org.dromara.patrol.entity.FaceLibraryPersonDto;
import org.dromara.patrol.entity.GpsLocationDto;
import org.dromara.patrol.entity.HeartbeatAckDto;
import org.dromara.patrol.entity.HeartbeatRequestDto;
import org.dromara.patrol.entity.IntercomSessionDto;
import org.dromara.patrol.entity.IntercomSessionRequestDto;
import org.dromara.patrol.entity.IntercomSignalDto;
import org.dromara.patrol.entity.IntercomSignalRequestDto;
import org.dromara.patrol.entity.LoginRequestDto;
import org.dromara.patrol.entity.MediaFileDto;
import org.dromara.patrol.entity.MediaUploadTaskCreateDto;
import org.dromara.patrol.entity.MediaUploadTaskDto;
import org.dromara.patrol.entity.PageEnvelope;
import org.dromara.patrol.entity.PatrolAreaDto;
import org.dromara.patrol.entity.PatrolGeoPointDto;
import org.dromara.patrol.entity.PatrolMessageDto;
import org.dromara.patrol.entity.ScannedDeviceDto;
import org.dromara.patrol.entity.SosEventDto;
import org.dromara.patrol.entity.StreamRelayRequestDto;
import org.dromara.patrol.entity.StreamRelayStateDto;
import org.dromara.patrol.entity.TransferRequestDto;
import org.dromara.patrol.entity.UploadAttachmentDto;
import org.dromara.patrol.entity.UserProfileDto;
import org.dromara.patrol.entity.VersionCheckDto;
import org.dromara.patrol.mapper.PatrolAppVersionMapper;
import org.dromara.patrol.mapper.PatrolAlertAttachmentMapper;
import org.dromara.patrol.mapper.PatrolAlertDispositionMapper;
import org.dromara.patrol.mapper.PatrolAlertMapper;
import org.dromara.patrol.mapper.PatrolAreaMapper;
import org.dromara.patrol.mapper.PatrolAuditLogMapper;
import org.dromara.patrol.mapper.PatrolControlPersonMapper;
import org.dromara.patrol.mapper.PatrolDeviceMapper;
import org.dromara.patrol.mapper.PatrolDeviceCommandMapper;
import org.dromara.patrol.mapper.PatrolDeviceBindingMapper;
import org.dromara.patrol.mapper.PatrolDeviceConfigMapper;
import org.dromara.patrol.mapper.PatrolDeviceEventMapper;
import org.dromara.patrol.mapper.PatrolLocationTrackMapper;
import org.dromara.patrol.mapper.PatrolMediaMapper;
import org.dromara.patrol.mapper.PatrolMediaUploadTaskMapper;
import org.dromara.patrol.mapper.PatrolMessageMapper;
import org.dromara.patrol.mapper.PatrolMessageReceiptMapper;
import org.dromara.patrol.mapper.PatrolSosEventMapper;
import org.dromara.patrol.service.IPatrolAppService;
import org.dromara.patrol.service.PatrolRealtimePublisher;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.vo.SysDeptVo;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.system.service.ISysDeptService;
import org.dromara.system.service.ISysOssService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
    private static final long DEFAULT_CHUNK_SIZE = 8L * 1024L * 1024L;
    private static final Duration INTERCOM_TTL = Duration.ofMinutes(30);
    private static final String INTERCOM_SESSION_PREFIX = "patrol:intercom:session:";
    private static final String INTERCOM_PENDING_PREFIX = "patrol:intercom:pending:";
    private static final String INTERCOM_SIGNAL_PREFIX = "patrol:intercom:signals:";

    private final SysUserMapper userMapper;
    private final ISysDeptService deptService;
    private final ISysOssService ossService;
    private final PatrolDeviceMapper deviceMapper;
    private final PatrolDeviceCommandMapper commandMapper;
    private final PatrolDeviceBindingMapper deviceBindingMapper;
    private final PatrolDeviceConfigMapper deviceConfigMapper;
    private final PatrolDeviceEventMapper deviceEventMapper;
    private final PatrolLocationTrackMapper locationTrackMapper;
    private final PatrolAlertMapper alertMapper;
    private final PatrolAlertAttachmentMapper alertAttachmentMapper;
    private final PatrolAlertDispositionMapper alertDispositionMapper;
    private final PatrolMediaMapper mediaMapper;
    private final PatrolMediaUploadTaskMapper mediaUploadTaskMapper;
    private final PatrolMessageMapper messageMapper;
    private final PatrolMessageReceiptMapper messageReceiptMapper;
    private final PatrolAuditLogMapper auditLogMapper;
    private final PatrolAreaMapper areaMapper;
    private final PatrolSosEventMapper sosEventMapper;
    private final PatrolAppVersionMapper appVersionMapper;
    private final PatrolControlPersonMapper controlPersonMapper;
    private final PatrolRealtimePublisher realtimePublisher;

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
            saveDeviceBinding(device);
            saveDeviceEvent(deviceId, "BIND", "INFO", "设备绑定上线", device.getDeviceName() + " 已绑定并同步云端连接状态");
            cacheDevice(device);
            realtimePublisher.publish("DEVICE_STATUS", "devices", "设备绑定上线", device.getDeviceId() + " 已绑定并在线", device.getDeviceId(),
                realtimePublisher.payload("deviceId", device.getDeviceId(), "online", device.getOnline(), "batteryPercent", device.getBatteryPercent()));
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
            saveDeviceEvent(deviceId, "COMMAND", "INFO", "App同步设备指令", request.getCommand());
            cacheDevice(device);
            realtimePublisher.publish("DEVICE_EVENT", "devices", "App同步设备指令", deviceId + " " + request.getCommand(), deviceId,
                realtimePublisher.payload("deviceId", deviceId, "command", request.getCommand(), "status", "ACKED"));
            return toDeviceStatus(device);
        });
    }

    @Override
    public DeviceCapabilitiesDto deviceCapabilities(String deviceId) {
        return TenantHelper.dynamic(TENANT_ID, () -> toCapabilitiesDto(getOrCreateDeviceConfig(deviceId)));
    }

    @Override
    public DeviceWifiStateDto deviceWifi(String deviceId) {
        return TenantHelper.dynamic(TENANT_ID, () -> toWifiDto(getOrCreateDeviceConfig(deviceId)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceWifiStateDto configureWifi(String deviceId, DeviceWifiStateDto request) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolDeviceConfig config = getOrCreateDeviceConfig(deviceId);
            config.setWifiEnabled(request.isEnabled());
            config.setWifiSsid(blankToDefault(request.getSsid(), ""));
            config.setWifiPasswordConfigured(request.isPasswordConfigured() || request.isEnabled());
            config.setWifiConnected(request.isEnabled() && !blankToDefault(request.getSsid(), "").isBlank());
            deviceConfigMapper.insertOrUpdate(config);
            saveDeviceEvent(deviceId, "WIFI", "INFO", "App同步设备Wi-Fi配置", config.getWifiSsid());
            saveAudit("DEVICE", "App配置设备Wi-Fi", deviceId, "SUCCESS");
            realtimePublisher.publish("DEVICE_EVENT", "devices", "设备Wi-Fi配置更新", deviceId + " 已同步 Wi-Fi 配置", deviceId,
                realtimePublisher.payload("deviceId", deviceId, "wifiEnabled", config.getWifiEnabled(), "ssid", config.getWifiSsid()));
            return toWifiDto(config);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceAdvancedSettingsDto applySettings(String deviceId, DeviceAdvancedSettingsDto request) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolDeviceConfig config = getOrCreateDeviceConfig(deviceId);
            config.setVideoWidth(request.getVideoWidth());
            config.setVideoHeight(request.getVideoHeight());
            config.setVideoFrameRate(request.getVideoFrameRate());
            config.setRecordingDurationSeconds(request.getRecordingDurationSeconds());
            config.setVerticalRecording(request.isVerticalRecording());
            config.setEnhancedSound(request.isEnhancedSound());
            config.setBrightnessLevel(request.getBrightnessLevel());
            deviceConfigMapper.insertOrUpdate(config);
            saveDeviceEvent(deviceId, "SETTINGS", "INFO", "App同步设备高级设置", "video=" + request.getVideoWidth() + "x" + request.getVideoHeight() + "@" + request.getVideoFrameRate());
            saveAudit("DEVICE", "App同步设备高级设置", deviceId, "SUCCESS");
            realtimePublisher.publish("DEVICE_EVENT", "devices", "设备高级设置更新", deviceId + " 已同步高级设置", deviceId,
                realtimePublisher.payload("deviceId", deviceId, "videoWidth", request.getVideoWidth(), "videoHeight", request.getVideoHeight(), "videoFrameRate", request.getVideoFrameRate()));
            return toSettingsDto(config);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceControlResultDto startRealtimeAudioSync(String deviceId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolDeviceConfig config = getOrCreateDeviceConfig(deviceId);
            config.setRealtimeAudioSyncing(true);
            deviceConfigMapper.insertOrUpdate(config);
            saveDeviceEvent(deviceId, "REALTIME_AUDIO", "INFO", "实时音频同步开始", "App端已开启实时音频同步");
            realtimePublisher.publish("DEVICE_EVENT", "devices", "实时音频同步开始", deviceId + " 已开启实时音频同步", deviceId,
                realtimePublisher.payload("deviceId", deviceId, "realtimeAudioSyncing", true));
            return new DeviceControlResultDto(true, "STARTED", "实时音频同步已开始");
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceControlResultDto stopRealtimeAudioSync(String deviceId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolDeviceConfig config = getOrCreateDeviceConfig(deviceId);
            config.setRealtimeAudioSyncing(false);
            deviceConfigMapper.insertOrUpdate(config);
            saveDeviceEvent(deviceId, "REALTIME_AUDIO", "INFO", "实时音频同步停止", "App端已停止实时音频同步");
            realtimePublisher.publish("DEVICE_EVENT", "devices", "实时音频同步停止", deviceId + " 已停止实时音频同步", deviceId,
                realtimePublisher.payload("deviceId", deviceId, "realtimeAudioSyncing", false));
            return new DeviceControlResultDto(true, "STOPPED", "实时音频同步已停止");
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceControlResultDto notifyMediaSyncCompleted(String deviceId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolDeviceConfig config = getOrCreateDeviceConfig(deviceId);
            config.setLastMediaSyncAt(new Date());
            deviceConfigMapper.insertOrUpdate(config);
            saveDeviceEvent(deviceId, "MEDIA_SYNC", "INFO", "媒体同步完成", "App端已完成媒体同步");
            realtimePublisher.publish("DEVICE_EVENT", "media", "媒体同步完成", deviceId + " 已完成媒体同步", deviceId,
                realtimePublisher.payload("deviceId", deviceId, "lastMediaSyncAt", config.getLastMediaSyncAt()));
            return new DeviceControlResultDto(true, "DONE", "媒体同步完成状态已记录");
        });
    }

    @Override
    public PageEnvelope<AlertDto> alerts(int page, int pageSize) {
        return TenantHelper.dynamic(TENANT_ID, () -> page(alertMapper.selectList().stream().map(this::toAlertDto).toList(), page, pageSize));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlertDto acknowledgeAlert(String alertId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            AlertDto alert = updateAlert(alertId, "HANDLING", null, null, null);
            saveAlertDisposition(alertId, "ACK", "HANDLING", null, 0);
            realtimePublisher.publish("ALERT_UPDATED", "alerts", "预警已确认", alertId + " 已进入处置中", alertId,
                realtimePublisher.payload("alertId", alertId, "status", "HANDLING"));
            return alert;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlertDto closeAlert(String alertId, AlertCloseRequestDto request) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            AlertDto alert = updateAlert(alertId, "CLOSED", request.getResult() + "：" + request.getNote(), request.getResult(), request.getOperatorId());
            saveAlertAttachments(alertId, request.getAttachments());
            saveAlertDisposition(alertId, "CLOSE", request.getResult(), request.getNote(), request.getAttachments() == null ? 0 : request.getAttachments().size());
            saveAudit("ALERT", "App关闭预警", alertId, "SUCCESS");
            realtimePublisher.publish("ALERT_UPDATED", "alerts", "预警已关闭", alertId + " 处置结果：" + request.getResult(), alertId,
                realtimePublisher.payload("alertId", alertId, "status", "CLOSED", "result", request.getResult()));
            return alert;
        });
    }

    @Override
    public PageEnvelope<MediaFileDto> mediaFiles(String side, int page, int pageSize) {
        return TenantHelper.dynamic(TENANT_ID, () -> page(mediaMapper.selectList(new LambdaQueryWrapper<PatrolMedia>().eq(PatrolMedia::getStorageSide, side)).stream().map(this::toMediaDto).toList(), page, pageSize));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaFileDto uploadMedia(MultipartFile file, String storageSide, String bizType, String bizId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            String sha256 = sha256(file);
            SysOssVo oss = ossService.upload(file);
            String fileId = "FILE-" + (oss.getOssId() == null ? IdUtil.fastSimpleUUID() : oss.getOssId());
            LoginUser loginUser = LoginHelper.getLoginUser();
            String deviceId = currentBoundDeviceId();
            String uploadedAt = Date.from(Instant.now()).toString();
            PatrolMedia media = new PatrolMedia();
            media.setMediaId(IdUtil.getSnowflakeNextId());
            media.setTenantId(TENANT_ID);
            media.setFileId(fileId);
            media.setFileName(blankToDefault(file.getOriginalFilename(), oss.getOriginalName()));
            media.setMediaType(mediaType(media.getFileName(), file.getContentType()));
            media.setCapturedAt(uploadedAt);
            media.setSizeText(sizeText(file.getSize()));
            media.setFileSizeBytes(file.getSize());
            media.setMimeType(blankToDefault(file.getContentType(), "application/octet-stream"));
            media.setSha256Verified(true);
            media.setStorageSide(blankToDefault(storageSide, "PHONE"));
            media.setTransferStatus("DONE");
            media.setProgress(1F);
            media.setContentUri("/files/" + fileId + "/download");
            media.setOssId(oss.getOssId());
            media.setBucketName("ruoyi");
            media.setObjectKey(oss.getFileName());
            media.setSha256(sha256);
            media.setWatermarkToken(watermarkToken(sha256, loginUser.getUsername(), deviceId, uploadedAt));
            media.setBadgeNo(loginUser.getUsername());
            media.setOfficerName(blankToDefault(loginUser.getNickname(), loginUser.getUsername()));
            media.setDeviceId(deviceId);
            media.setBizType(blankToDefault(bizType, "MEDIA"));
            media.setBizId(blankToDefault(bizId, fileId));
            media.setEvidenceSource("APP_UPLOAD");
            media.setDelFlag("0");
            mediaMapper.insert(media);
            saveAudit("MEDIA", "App上传媒体文件", blankToDefault(bizId, fileId), "SUCCESS");
            realtimePublisher.publish("MEDIA_UPLOADED", "media", "媒体证据已上传", media.getFileName(), fileId,
                realtimePublisher.payload("fileId", fileId, "deviceId", deviceId, "mediaType", media.getMediaType(), "sha256", sha256));
            return toMediaDto(media);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaUploadTaskDto createMediaUploadTask(MediaUploadTaskCreateDto request) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            validateUploadTask(request);
            LoginUser loginUser = LoginHelper.getLoginUser();
            String taskId = "UP-" + IdUtil.fastSimpleUUID();
            long chunkSize = request.getChunkSizeBytes() > 0 ? request.getChunkSizeBytes() : DEFAULT_CHUNK_SIZE;
            int totalChunks = request.getTotalChunks() > 0 ? request.getTotalChunks() : (int) Math.ceil(request.getFileSizeBytes() * 1D / chunkSize);
            PatrolMediaUploadTask task = new PatrolMediaUploadTask();
            task.setTaskId(taskId);
            task.setTenantId(TENANT_ID);
            String fileName = safeFileName(request.getFileName());
            task.setFileName(fileName);
            task.setMediaType(blankToDefault(request.getMediaType(), mediaType(fileName, request.getMimeType())));
            task.setMimeType(blankToDefault(request.getMimeType(), "application/octet-stream"));
            task.setFileSizeBytes(request.getFileSizeBytes());
            task.setChunkSizeBytes(chunkSize);
            task.setTotalChunks(totalChunks);
            task.setUploadedChunks(0);
            task.setUploadedBytes(0L);
            task.setExpectedSha256(blankToDefault(request.getSha256(), ""));
            task.setStorageSide(blankToDefault(request.getStorageSide(), "PHONE"));
            task.setBizType(blankToDefault(request.getBizType(), "MEDIA"));
            task.setBizId(blankToDefault(request.getBizId(), taskId));
            task.setStatus("INIT");
            task.setProgress(0F);
            task.setTempDir(uploadTaskDir(taskId).toString());
            task.setBadgeNo(loginUser.getUsername());
            task.setOfficerName(blankToDefault(loginUser.getNickname(), loginUser.getUsername()));
            task.setDeviceId(currentBoundDeviceId());
            task.setDelFlag("0");
            try {
                Files.createDirectories(uploadTaskDir(taskId));
            } catch (IOException e) {
                throw new ServiceException("创建上传任务目录失败：" + e.getMessage());
            }
            mediaUploadTaskMapper.insert(task);
            return toUploadTaskDto(task);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaUploadTaskDto uploadMediaChunk(String taskId, int chunkIndex, MultipartFile chunk) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolMediaUploadTask task = getUploadTask(taskId);
            if ("DONE".equals(task.getStatus())) {
                return toUploadTaskDto(task);
            }
            if ("CANCELLED".equals(task.getStatus())) {
                throw new ServiceException("上传任务已取消");
            }
            if (chunkIndex < 0 || chunkIndex >= task.getTotalChunks()) {
                throw new ServiceException("分片序号超出任务范围");
            }
            if (chunk == null || chunk.isEmpty()) {
                throw new ServiceException("上传分片不能为空");
            }
            Path partPath = uploadTaskDir(taskId).resolve(chunkIndex + ".part");
            try {
                Files.createDirectories(partPath.getParent());
                Files.copy(chunk.getInputStream(), partPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                markUploadTaskFailed(task, "保存上传分片失败：" + e.getMessage());
                throw new ServiceException(task.getErrorMessage());
            }
            refreshUploadProgress(task);
            task.setStatus(task.getUploadedChunks() >= task.getTotalChunks() ? "UPLOADED" : "UPLOADING");
            mediaUploadTaskMapper.updateById(task);
            if ("UPLOADED".equals(task.getStatus())) {
                realtimePublisher.publish("MEDIA_UPLOAD_PROGRESS", "media", "媒体分片上传完成", task.getFileName() + " 分片已全部上传", taskId,
                    realtimePublisher.payload("taskId", taskId, "status", task.getStatus(), "progress", task.getProgress(), "uploadedChunks", task.getUploadedChunks()));
            }
            return toUploadTaskDto(task);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaUploadTaskDto completeMediaUploadTask(String taskId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolMediaUploadTask task = getUploadTask(taskId);
            if ("DONE".equals(task.getStatus())) {
                return toUploadTaskDto(task);
            }
            if ("CANCELLED".equals(task.getStatus())) {
                throw new ServiceException("上传任务已取消");
            }
            task.setStatus("MERGING");
            task.setProgress(0.99F);
            mediaUploadTaskMapper.updateById(task);
            File merged = mergeUploadTask(task);
            String actualSha256 = sha256(merged);
            if (task.getExpectedSha256() != null && !task.getExpectedSha256().isBlank() && !task.getExpectedSha256().equalsIgnoreCase(actualSha256)) {
                markUploadTaskFailed(task, "SHA-256不一致");
                throw new ServiceException("SHA-256不一致");
            }
            SysOssVo oss = ossService.upload(merged);
            String fileId = "FILE-" + (oss.getOssId() == null ? IdUtil.fastSimpleUUID() : oss.getOssId());
            String uploadedAt = Date.from(Instant.now()).toString();
            PatrolMedia media = new PatrolMedia();
            media.setMediaId(IdUtil.getSnowflakeNextId());
            media.setTenantId(TENANT_ID);
            media.setFileId(fileId);
            media.setFileName(task.getFileName());
            media.setMediaType(task.getMediaType());
            media.setCapturedAt(uploadedAt);
            media.setSizeText(sizeText(value(task.getFileSizeBytes(), merged.length())));
            media.setFileSizeBytes(value(task.getFileSizeBytes(), merged.length()));
            media.setMimeType(task.getMimeType());
            media.setSha256Verified(true);
            media.setStorageSide(task.getStorageSide());
            media.setTransferStatus("DONE");
            media.setProgress(1F);
            media.setContentUri("/files/" + fileId + "/download");
            media.setOssId(oss.getOssId());
            media.setBucketName("ruoyi");
            media.setObjectKey(oss.getFileName());
            media.setSha256(actualSha256);
            media.setWatermarkToken(watermarkToken(actualSha256, task.getBadgeNo(), task.getDeviceId(), uploadedAt));
            media.setBadgeNo(task.getBadgeNo());
            media.setOfficerName(task.getOfficerName());
            media.setDeviceId(task.getDeviceId());
            media.setBizType(task.getBizType());
            media.setBizId(task.getBizId());
            media.setEvidenceSource("APP_CHUNK_UPLOAD");
            media.setDelFlag("0");
            mediaMapper.insert(media);

            task.setFileId(fileId);
            task.setActualSha256(actualSha256);
            task.setStatus("DONE");
            task.setProgress(1F);
            task.setCompletedAt(new Date());
            task.setErrorMessage(null);
            mediaUploadTaskMapper.updateById(task);
            deleteQuietly(uploadTaskDir(taskId));
            saveAudit("MEDIA", "App分片上传媒体文件", blankToDefault(task.getBizId(), fileId), "SUCCESS");
            realtimePublisher.publish("MEDIA_UPLOAD_DONE", "media", "媒体分片上传入库", task.getFileName() + " 已合并并入库", fileId,
                realtimePublisher.payload("taskId", taskId, "fileId", fileId, "deviceId", task.getDeviceId(), "mediaType", task.getMediaType(), "sha256", actualSha256));
            return toUploadTaskDto(task);
        });
    }

    @Override
    public MediaUploadTaskDto mediaUploadTask(String taskId) {
        return TenantHelper.dynamic(TENANT_ID, () -> toUploadTaskDto(getUploadTask(taskId)));
    }

    @Override
    public PageEnvelope<MediaUploadTaskDto> mediaUploadTasks(int page, int pageSize) {
        return TenantHelper.dynamic(TENANT_ID, () -> page(mediaUploadTaskMapper.selectList(new LambdaQueryWrapper<PatrolMediaUploadTask>()
                .orderByDesc(PatrolMediaUploadTask::getCreateTime))
            .stream()
            .map(this::toUploadTaskDto)
            .toList(), page, pageSize));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaUploadTaskDto retryMediaUploadTask(String taskId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolMediaUploadTask task = getUploadTask(taskId);
            if ("DONE".equals(task.getStatus())) {
                return toUploadTaskDto(task);
            }
            if ("CANCELLED".equals(task.getStatus())) {
                throw new ServiceException("上传任务已取消，不能重试");
            }
            refreshUploadProgress(task);
            task.setStatus(task.getUploadedChunks() > 0 ? "UPLOADING" : "INIT");
            task.setErrorMessage(null);
            task.setActualSha256(null);
            mediaUploadTaskMapper.updateById(task);
            return toUploadTaskDto(task);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaUploadTaskDto cancelMediaUploadTask(String taskId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolMediaUploadTask task = getUploadTask(taskId);
            if ("DONE".equals(task.getStatus())) {
                return toUploadTaskDto(task);
            }
            deleteQuietly(uploadTaskDir(taskId));
            task.setStatus("CANCELLED");
            task.setErrorMessage("任务已取消");
            task.setProgress(0F);
            mediaUploadTaskMapper.updateById(task);
            saveAudit("MEDIA", "取消分片上传任务", taskId, "SUCCESS");
            realtimePublisher.publish("MEDIA_UPLOAD_CANCELLED", "media", "媒体上传任务已取消", task.getFileName(), taskId,
                realtimePublisher.payload("taskId", taskId, "status", "CANCELLED"));
            return toUploadTaskDto(task);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanExpiredMediaUploadTasks(int retentionHours) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            int safeHours = Math.max(retentionHours, 1);
            Date deadline = Date.from(Instant.now().minus(Duration.ofHours(safeHours)));
            List<PatrolMediaUploadTask> tasks = mediaUploadTaskMapper.selectList(new LambdaQueryWrapper<PatrolMediaUploadTask>()
                .notIn(PatrolMediaUploadTask::getStatus, List.of("DONE", "CANCELLED", "EXPIRED"))
                .lt(PatrolMediaUploadTask::getCreateTime, deadline));
            for (PatrolMediaUploadTask task : tasks) {
                deleteQuietly(uploadTaskDir(task.getTaskId()));
                task.setStatus("EXPIRED");
                task.setErrorMessage("上传任务已过期清理");
                mediaUploadTaskMapper.updateById(task);
            }
            if (!tasks.isEmpty()) {
                realtimePublisher.publish("MEDIA_UPLOAD_CLEANED", "media", "过期上传任务已清理", "已清理 " + tasks.size() + " 个过期上传任务", "media-upload-cleanup",
                    realtimePublisher.payload("cleaned", tasks.size(), "retentionHours", safeHours));
            }
            return tasks.size();
        });
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
            realtimePublisher.publish("MEDIA_DELETED", "media", deleted ? "媒体证据已删除" : "媒体删除失败", fileId, fileId,
                realtimePublisher.payload("fileId", fileId, "side", side, "deleted", deleted));
            return deleted;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean verifyMedia(String fileId) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            List<PatrolMedia> files = mediaMapper.selectList(new LambdaQueryWrapper<PatrolMedia>().eq(PatrolMedia::getFileId, fileId));
            files.forEach(file -> {
                boolean currentVerified = verifyStoredMedia(file);
                file.setSha256Verified(currentVerified);
                mediaMapper.updateById(file);
            });
            boolean verified = files.stream().anyMatch(item -> Boolean.TRUE.equals(item.getSha256Verified()));
            saveAudit("MEDIA", "App校验媒体文件", fileId, verified ? "SUCCESS" : "FAILED");
            realtimePublisher.publish("MEDIA_VERIFIED", "media", verified ? "媒体证据校验通过" : "媒体证据校验失败", fileId, fileId,
                realtimePublisher.payload("fileId", fileId, "verified", verified));
            return verified;
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
            boolean locationUpdated = request.getLatitude() != null && request.getLongitude() != null;
            if (locationUpdated) {
                device.setLatitude(request.getLatitude());
                device.setLongitude(request.getLongitude());
                device.setAddress(blankToDefault(request.getAddress(), device.getAddress()));
                saveLocationTrack(device, request);
            }
            deviceMapper.insertOrUpdate(device);
            if (!request.isOnline()) {
                saveDeviceEvent(device.getDeviceId(), "OFFLINE", "WARNING", "设备离线", "心跳上报 offline");
            } else if (request.getBatteryPercent() < 20) {
                saveDeviceEvent(device.getDeviceId(), "LOW_BATTERY", "WARNING", "设备低电量", "当前电量 " + request.getBatteryPercent() + "%");
            }
            cacheDevice(device);
            realtimePublisher.publish(locationUpdated ? "DEVICE_LOCATION" : "DEVICE_STATUS", locationUpdated ? "map" : "devices",
                locationUpdated ? "设备位置更新" : "设备状态更新", device.getDeviceId() + " 心跳已上报", device.getDeviceId(),
                realtimePublisher.payload("deviceId", device.getDeviceId(), "online", device.getOnline(), "batteryPercent", device.getBatteryPercent(),
                    "signalBars", device.getSignalBars(), "latitude", device.getLatitude(), "longitude", device.getLongitude(), "address", device.getAddress()));
        });
        return new HeartbeatAckDto(request.isOnline(), Instant.now().getEpochSecond(), 15);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageEnvelope<PatrolMessageDto> messages(String targetId, int page, int pageSize) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            String safeTargetId = blankToDefault(targetId, LoginHelper.getUsername());
            List<PatrolMessageDto> items = new ArrayList<>();
            for (PatrolMessage message : messageMapper.selectList(new LambdaQueryWrapper<PatrolMessage>().orderByDesc(PatrolMessage::getSentAt))) {
                PatrolMessageReceipt receipt = visibleReceipt(message.getMessageId(), safeTargetId);
                if (receipt != null) {
                    markReceiptDelivered(receipt);
                    PatrolMessage syncedMessage = syncMessageDeliverySummary(message.getMessageId());
                    items.add(toMessageDto(syncedMessage == null ? message : syncedMessage, receipt));
                } else if (isMessageVisible(message, safeTargetId)) {
                    items.add(toMessageDto(message, null));
                }
            }
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
            PatrolMessageReceipt receipt = visibleReceipt(messageId, LoginHelper.getUsername());
            if (receipt != null) {
                receipt.setDeliveryStatus("READ");
                if (receipt.getDeliveredAt() == null) {
                    receipt.setDeliveredAt(new Date());
                }
                receipt.setReadAt(new Date());
                receipt.setLastPullAt(new Date());
                messageReceiptMapper.updateById(receipt);
                message = syncMessageDeliverySummary(messageId);
            } else {
                int total = Math.max(value(message.getTotalCount(), 0), 1);
                int read = Math.min(value(message.getReadCount(), 0) + 1, total);
                message.setReadCount(read);
                if (read >= total) {
                    message.setStatus("READ");
                }
                messageMapper.updateById(message);
            }
            saveAudit("MESSAGE", "App读取指挥消息", messageId, "SUCCESS");
            realtimePublisher.publish("MESSAGE_READ", "messages", "指挥消息已读", message.getTitle(), messageId,
                realtimePublisher.payload("messageId", messageId, "readCount", message.getReadCount(), "totalCount", message.getTotalCount(), "status", message.getStatus()));
            return toMessageDto(message, receipt);
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
    @Transactional(rollbackFor = Exception.class)
    public IntercomSessionDto createIntercomSession(IntercomSessionRequestDto request) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            String deviceId = blankToDefault(request.getDeviceId(), DEVICE_ID);
            PatrolDevice device = deviceMapper.selectById(deviceId);
            if (device == null) {
                throw new ServiceException("设备不存在：" + deviceId);
            }
            long now = System.currentTimeMillis();
            String sessionId = "IC-" + IdUtil.fastSimpleUUID();
            IntercomSessionDto session = new IntercomSessionDto(
                sessionId,
                deviceId,
                "WAITING_APP",
                blankToDefault(request.getMode(), "FULL_DUPLEX"),
                "/api/v1/intercom/sessions/" + sessionId + "/signals",
                "BLUETOOTH_HEADSET_SCO_PREFERRED",
                List.of("stun:turn.patrollink.local:3478", "turn:turn.patrollink.local:3478?transport=udp"),
                now,
                now + INTERCOM_TTL.toMillis(),
                "已创建 WebRTC/VoIP 对讲会话，等待 App 接入蓝牙耳机音频路由"
            );
            saveIntercomSession(session);
            RedisUtils.setCacheObject(INTERCOM_PENDING_PREFIX + deviceId, sessionId, INTERCOM_TTL);
            saveDeviceEvent(deviceId, "INTERCOM", "INFO", "对讲会话创建", "Web端发起WebRTC/VoIP对讲，会话：" + sessionId);
            saveAudit("INTERCOM", "创建WebRTC/VoIP对讲会话", sessionId, "SUCCESS");
            realtimePublisher.publish("INTERCOM_SESSION", "dispatch", "对讲会话已创建", deviceId + " 等待 App 接入", sessionId,
                realtimePublisher.payload("sessionId", sessionId, "deviceId", deviceId, "state", session.getState(), "audioRoute", session.getAudioRoute()));
            return session;
        });
    }

    @Override
    public IntercomSessionDto pendingIntercomSession(String deviceId) {
        String sessionId = RedisUtils.getCacheObject(INTERCOM_PENDING_PREFIX + blankToDefault(deviceId, DEVICE_ID));
        if (sessionId == null) {
            return null;
        }
        IntercomSessionDto session = intercomSession(sessionId);
        return session == null || "CLOSED".equals(session.getState()) ? null : session;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IntercomSessionDto acceptIntercomSession(String sessionId) {
        IntercomSessionDto session = requireIntercomSession(sessionId);
        session.setState("SIGNALING");
        session.setMessage("App 已接入，会话进入 WebRTC 信令交换");
        saveIntercomSession(session);
        PatrolDevice device = deviceMapper.selectById(session.getDeviceId());
        if (device != null) {
            device.setTalking(true);
            deviceMapper.updateById(device);
            cacheDevice(device);
        }
        saveDeviceEvent(session.getDeviceId(), "INTERCOM", "INFO", "App接入对讲会话", "蓝牙耳机音频路由准备接入：" + sessionId);
        realtimePublisher.publish("INTERCOM_SESSION", "dispatch", "App 已接入对讲", session.getDeviceId() + " 开始信令交换", sessionId,
            realtimePublisher.payload("sessionId", sessionId, "deviceId", session.getDeviceId(), "state", session.getState()));
        return session;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IntercomSessionDto closeIntercomSession(String sessionId) {
        IntercomSessionDto session = requireIntercomSession(sessionId);
        session.setState("CLOSED");
        session.setMessage("对讲会话已关闭");
        saveIntercomSession(session);
        RedisUtils.deleteObject(INTERCOM_PENDING_PREFIX + session.getDeviceId());
        PatrolDevice device = deviceMapper.selectById(session.getDeviceId());
        if (device != null) {
            device.setTalking(false);
            deviceMapper.updateById(device);
            cacheDevice(device);
        }
        saveDeviceEvent(session.getDeviceId(), "INTERCOM", "INFO", "对讲会话关闭", "WebRTC/VoIP对讲会话关闭：" + sessionId);
        saveAudit("INTERCOM", "关闭WebRTC/VoIP对讲会话", sessionId, "SUCCESS");
        realtimePublisher.publish("INTERCOM_SESSION", "dispatch", "对讲会话已关闭", session.getDeviceId() + " 对讲结束", sessionId,
            realtimePublisher.payload("sessionId", sessionId, "deviceId", session.getDeviceId(), "state", session.getState()));
        return session;
    }

    @Override
    public IntercomSignalDto sendIntercomSignal(String sessionId, IntercomSignalRequestDto request) {
        IntercomSessionDto session = requireIntercomSession(sessionId);
        if ("CLOSED".equals(session.getState())) {
            throw new ServiceException("对讲会话已关闭");
        }
        long now = System.currentTimeMillis();
        IntercomSignalDto signal = new IntercomSignalDto(
            "SIG-" + IdUtil.fastSimpleUUID(),
            sessionId,
            blankToDefault(request.getSender(), "UNKNOWN"),
            blankToDefault(request.getType(), "message"),
            blankToDefault(request.getPayload(), ""),
            now
        );
        RedisUtils.addCacheList(INTERCOM_SIGNAL_PREFIX + sessionId, signal);
        RedisUtils.getClient().getList(INTERCOM_SIGNAL_PREFIX + sessionId).expire(INTERCOM_TTL);
        if ("hangup".equalsIgnoreCase(signal.getType())) {
            closeIntercomSession(sessionId);
        } else if ("answer".equalsIgnoreCase(signal.getType())) {
            session.setState("ACTIVE");
            session.setMessage("WebRTC 音频流已建立");
            saveIntercomSession(session);
        } else if (!"ACTIVE".equals(session.getState())) {
            session.setState("SIGNALING");
            session.setMessage("WebRTC 信令交换中");
            saveIntercomSession(session);
        }
        realtimePublisher.publish("INTERCOM_SIGNAL", "dispatch", "对讲信令已转发", signal.getSender() + ":" + signal.getType(), sessionId,
            realtimePublisher.payload("sessionId", sessionId, "signalId", signal.getSignalId(), "sender", signal.getSender(), "type", signal.getType()));
        return signal;
    }

    @Override
    public List<IntercomSignalDto> intercomSignals(String sessionId, String afterSignalId) {
        requireIntercomSession(sessionId);
        List<IntercomSignalDto> signals = RedisUtils.getCacheList(INTERCOM_SIGNAL_PREFIX + sessionId);
        if (afterSignalId == null || afterSignalId.isBlank()) {
            return signals;
        }
        int index = -1;
        for (int i = 0; i < signals.size(); i++) {
            if (afterSignalId.equals(signals.get(i).getSignalId())) {
                index = i;
                break;
            }
        }
        return index < 0 || index + 1 >= signals.size() ? List.of() : signals.subList(index + 1, signals.size());
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
            realtimePublisher.publish("SOS_ACTIVE", "sos", "SOS求助已激活", blankToDefault(location.getAddress(), "未知位置"), event.getSosId(),
                realtimePublisher.payload("sosId", event.getSosId(), "latitude", event.getLatitude(), "longitude", event.getLongitude(), "address", event.getAddress(), "phase", event.getPhase()));
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
            realtimePublisher.publish("SOS_CANCELLED", "sos", "SOS求助已取消", event.getSosId(), event.getSosId(),
                realtimePublisher.payload("sosId", event.getSosId(), "phase", event.getPhase()));
            return toSosDto(event);
        });
    }

    @Override
    public VersionCheckDto checkVersion(int currentVersionCode) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            PatrolAppVersion latest = appVersionMapper.selectList(new LambdaQueryWrapper<PatrolAppVersion>()
                    .eq(PatrolAppVersion::getStatus, "PUBLISHED"))
                .stream()
                .max(Comparator.comparing(PatrolAppVersion::getVersionCode))
                .orElse(null);
            if (latest == null) {
                return new VersionCheckDto(currentVersionCode, "1.0.0", false, List.of("当前已是最新版本"), null, null);
            }
            return new VersionCheckDto(
                value(latest.getVersionCode(), currentVersionCode),
                blankToDefault(latest.getVersionName(), "1.0.0"),
                Boolean.TRUE.equals(latest.getForceUpdate()) && value(latest.getVersionCode(), 0) > currentVersionCode,
                changelog(latest.getChangelog()),
                latest.getDownloadUrl(),
                latest.getSha256()
            );
        });
    }

    @Override
    public FaceLibraryPackageDto faceLibraryPackage(String deviceId, String currentVersion, boolean force) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            Date now = new Date();
            List<PatrolControlPerson> persons = controlPersonMapper.selectList(new LambdaQueryWrapper<PatrolControlPerson>()
                .eq(PatrolControlPerson::getStatus, "ENABLED")
                .and(wrapper -> wrapper.isNull(PatrolControlPerson::getExpiresAt).or().gt(PatrolControlPerson::getExpiresAt, now))
                .orderByAsc(PatrolControlPerson::getControlId));
            String version = faceLibraryVersion(persons);
            if (!force && version.equals(currentVersion)) {
                return new FaceLibraryPackageDto(version, "PLBackend", true, "opencv-zoo-yunet+sface", blankToDefault(deviceId, DEVICE_ID), true, Instant.now().toEpochMilli(), List.of());
            }
            List<FaceLibraryPersonDto> records = persons.stream()
                .map(this::toFaceLibraryPerson)
                .toList();
            return new FaceLibraryPackageDto(version, "PLBackend", true, "opencv-zoo-yunet+sface", blankToDefault(deviceId, DEVICE_ID), false, Instant.now().toEpochMilli(), records);
        });
    }

    @Override
    public DeviceControlResultDto acknowledgeFaceLibrary(FaceLibraryAckRequestDto request) {
        String deviceId = blankToDefault(request.getDeviceId(), DEVICE_ID);
        String message = "人脸库同步确认 version=" + blankToDefault(request.getVersion(), "-")
            + " applied=" + value(request.getApplied(), 0)
            + " pending=" + value(request.getPending(), 0)
            + " failed=" + value(request.getFailed(), 0);
        saveDeviceEvent(deviceId, "FACE_LIBRARY_SYNC", value(request.getFailed(), 0) > 0 ? "WARN" : "INFO", "小脑人脸库同步确认", message);
        return new DeviceControlResultDto(true, "FACE_LIBRARY_SYNC_ACK", message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlertDto reportCerebellumFaceAlert(CerebellumFaceAlertRequestDto request) {
        return TenantHelper.dynamic(TENANT_ID, () -> {
            String deviceId = blankToDefault(request.getDeviceId(), DEVICE_ID);
            String personId = blankToDefault(request.getPersonId(), "UNKNOWN");
            String alertId = blankToDefault(request.getAlertId(), "FACE-" + deviceId + "-" + personId + "-" + UUID.randomUUID());
            String displayName = blankToDefault(request.getDisplayName(), personId);
            String riskLevel = blankToDefault(request.getRiskLevel(), "MEDIUM");
            String level = "HIGH".equalsIgnoreCase(riskLevel) ? "HIGH" : "WARNING";
            Double similarity = request.getAverageSimilarity();
            String confidence = similarity == null ? "-" : Math.round(similarity * 100) + "%";

            PatrolAlert alert = getOrCreateAlert(alertId);
            alert.setTitle("重点人员疑似命中：" + displayName);
            alert.setLevel(level);
            alert.setStatus("PENDING");
            alert.setOccurredAt(blankToDefault(request.getOccurredAt(), Date.from(Instant.now()).toString()));
            alert.setLocationText(blankToDefault(request.getCameraId(), "边缘小脑实时流"));
            alert.setSource(deviceId);
            alert.setConfidence(confidence);
            alert.setDescription("边缘小脑多帧确认候选，人员=" + displayName
                + "，编号=" + personId
                + "，类别=" + blankToDefault(request.getCategory(), "-")
                + "，风险=" + riskLevel
                + "，命中帧=" + value(request.getVoteCount(), 0) + "/" + value(request.getConfirmFrames(), 0)
                + "，窗口=" + value(request.getWindowSeconds(), 0D) + "秒"
                + "，流=" + blankToDefault(request.getStreamId(), "-")
                + "，帧=" + blankToDefault(request.getFrameId(), "-")
                + "。结果仅作疑似候选，需人工确认。");
            alert.setDelFlag("0");
            alertMapper.insertOrUpdate(alert);

            saveDeviceEvent(deviceId, "FACE_ALERT", level, "重点人员疑似命中", alert.getDescription());
            saveAlertDisposition(alertId, "CREATE", "PENDING", "边缘小脑上报疑似候选，等待人工确认", 0);
            saveAudit("ALERT", "小脑上报重点人员疑似命中", alertId, "SUCCESS");
            realtimePublisher.publish("ALERT_CREATED", "alerts", alert.getTitle(), alert.getDescription(), alertId,
                realtimePublisher.payload("alertId", alertId, "deviceId", deviceId, "personId", personId, "status", alert.getStatus(), "level", level));
            return toAlertDto(alert);
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

    private FaceLibraryPersonDto toFaceLibraryPerson(PatrolControlPerson person) {
        Date updatedAt = person.getFaceUpdatedAt() != null ? person.getFaceUpdatedAt() : person.getUpdateTime();
        if (updatedAt == null) {
            updatedAt = person.getCreateTime();
        }
        return new FaceLibraryPersonDto(
            person.getControlId(),
            person.getControlId(),
            person.getName(),
            person.getName(),
            person.getCategory(),
            person.getRiskLevel(),
            person.getStatus(),
            person.getSource(),
            person.getExpiresAt(),
            person.getFaceImageUrl(),
            person.getFaceImageSha256(),
            updatedAt
        );
    }

    private String faceLibraryVersion(List<PatrolControlPerson> persons) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (PatrolControlPerson person : persons) {
                Date updatedAt = person.getFaceUpdatedAt() != null ? person.getFaceUpdatedAt() : person.getUpdateTime();
                String payload = blankToDefault(person.getControlId(), "")
                    + "|" + blankToDefault(person.getStatus(), "")
                    + "|" + blankToDefault(person.getFaceImageSha256(), "")
                    + "|" + (updatedAt == null ? 0L : updatedAt.getTime());
                digest.update(payload.getBytes(StandardCharsets.UTF_8));
            }
            return "face-lib-" + hex(digest.digest()).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("生成人脸库版本失败：" + e.getMessage());
        }
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

    private PatrolDeviceConfig getOrCreateDeviceConfig(String deviceId) {
        PatrolDeviceConfig config = deviceConfigMapper.selectOne(new LambdaQueryWrapper<PatrolDeviceConfig>()
            .eq(PatrolDeviceConfig::getDeviceId, deviceId)
            .last("limit 1"));
        if (config != null) {
            return config;
        }
        PatrolDevice device = getOrCreateDevice(deviceId);
        config = new PatrolDeviceConfig();
        config.setConfigId("CFG-" + UUID.randomUUID());
        config.setTenantId(TENANT_ID);
        config.setDeviceId(deviceId);
        applyDefaultCapabilities(config, device.getDeviceType());
        config.setWifiEnabled("GLASSES".equals(device.getDeviceType()));
        config.setWifiSsid("GLASSES".equals(device.getDeviceType()) ? "PatrolLink-Device" : "");
        config.setWifiPasswordConfigured(false);
        config.setWifiConnected(Boolean.TRUE.equals(config.getWifiEnabled()));
        config.setVideoWidth(240);
        config.setVideoHeight(0);
        config.setVideoFrameRate(16);
        config.setRecordingDurationSeconds(24 * 60 * 60);
        config.setVerticalRecording(true);
        config.setEnhancedSound(true);
        config.setBrightnessLevel(2);
        config.setRealtimeAudioSyncing(false);
        config.setDelFlag("0");
        deviceConfigMapper.insert(config);
        return config;
    }

    private void applyDefaultCapabilities(PatrolDeviceConfig config, String deviceType) {
        boolean headset = "HEADSET".equals(deviceType);
        boolean glasses = "GLASSES".equals(deviceType);
        boolean recorder = "RECORDER".equals(deviceType);
        config.setSupportsGlasses(glasses);
        config.setSupportsEarphone(headset);
        config.setSupportsWifi(glasses);
        config.setSupportsFileTransfer(headset || glasses || recorder);
        config.setSupportsPhoto(headset || glasses || recorder);
        config.setSupportsVideo(headset || glasses || recorder);
        config.setSupportsAudioRecord(headset || recorder);
        config.setSupportsRealtimeAudio(headset);
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
        media.setMediaId(IdUtil.getSnowflakeNextId());
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
            media.setMediaId(IdUtil.getSnowflakeNextId());
            media.setTenantId(TENANT_ID);
            media.setFileId(original.getFileId());
        }
            media.setFileName(original.getFileName());
            media.setMediaType(original.getMediaType());
            media.setCapturedAt(original.getCapturedAt());
            media.setSizeText(original.getSizeText());
            media.setFileSizeBytes(original.getFileSizeBytes());
            media.setMimeType(original.getMimeType());
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
            media.setWatermarkToken(original.getWatermarkToken());
            media.setBadgeNo(original.getBadgeNo());
            media.setOfficerName(original.getOfficerName());
            media.setDeviceId(original.getDeviceId());
            media.setBizType(original.getBizType());
            media.setBizId(original.getBizId());
            media.setEvidenceSource(original.getEvidenceSource());
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

    private void saveIntercomSession(IntercomSessionDto session) {
        RedisUtils.setCacheObject(INTERCOM_SESSION_PREFIX + session.getSessionId(), session, INTERCOM_TTL);
    }

    private IntercomSessionDto intercomSession(String sessionId) {
        return RedisUtils.getCacheObject(INTERCOM_SESSION_PREFIX + sessionId);
    }

    private IntercomSessionDto requireIntercomSession(String sessionId) {
        IntercomSessionDto session = intercomSession(sessionId);
        if (session == null) {
            throw new ServiceException("对讲会话不存在或已过期：" + sessionId);
        }
        return session;
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

    private void saveDeviceBinding(PatrolDevice device) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        List<PatrolDeviceBinding> activeBindings = deviceBindingMapper.selectList(new LambdaQueryWrapper<PatrolDeviceBinding>()
            .eq(PatrolDeviceBinding::getDeviceId, device.getDeviceId())
            .eq(PatrolDeviceBinding::getBindStatus, "BOUND"));
        PatrolDeviceBinding current = activeBindings.stream()
            .filter(item -> ObjectUtil.equal(item.getUserId(), loginUser.getUserId()))
            .findFirst()
            .orElse(null);
        activeBindings.stream()
            .filter(item -> !ObjectUtil.equal(item.getUserId(), loginUser.getUserId()))
            .forEach(item -> {
                item.setBindStatus("UNBOUND");
                item.setUnboundAt(new Date());
                deviceBindingMapper.updateById(item);
            });

        if (current == null) {
            current = new PatrolDeviceBinding();
            current.setBindingId("BIND-" + UUID.randomUUID());
            current.setTenantId(TENANT_ID);
            current.setDeviceId(device.getDeviceId());
        }
        current.setUserId(loginUser.getUserId());
        current.setUserName(loginUser.getUsername());
        current.setNickName(blankToDefault(loginUser.getNickname(), loginUser.getUsername()));
        current.setDeptId(loginUser.getDeptId());
        current.setDeptName(blankToDefault(current.getDeptName(), blankToDefault(loginUser.getDeptName(), "未分配")));
        current.setBadgeNo(loginUser.getUsername());
        current.setBindStatus("BOUND");
        current.setBoundAt(new Date());
        current.setRemark("App端绑定");
        current.setDelFlag("0");
        deviceBindingMapper.insertOrUpdate(current);
    }

    private void saveDeviceEvent(String deviceId, String eventType, String eventLevel, String eventTitle, String eventDetail) {
        PatrolDeviceEvent event = new PatrolDeviceEvent();
        event.setTenantId(TENANT_ID);
        event.setEventId("EVT-" + UUID.randomUUID());
        event.setDeviceId(deviceId);
        event.setEventType(eventType);
        event.setEventLevel(eventLevel);
        event.setEventTitle(eventTitle);
        event.setEventDetail(eventDetail);
        event.setOccurredAt(new Date());
        event.setDelFlag("0");
        deviceEventMapper.insert(event);
    }

    private void saveAlertAttachments(String alertId, List<UploadAttachmentDto> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        attachments.forEach(item -> {
            PatrolAlertAttachment attachment = new PatrolAlertAttachment();
            attachment.setTenantId(TENANT_ID);
            attachment.setAttachmentId("ATT-" + UUID.randomUUID());
            attachment.setAlertId(alertId);
            attachment.setClientFileId(item.getClientFileId());
            attachment.setFileName(item.getFileName());
            attachment.setMimeType(item.getMimeType());
            attachment.setSizeBytes(item.getSizeBytes());
            attachment.setSource(item.getSource());
            attachment.setLocalUri(item.getLocalUri());
            attachment.setUploadIntent(item.getUploadIntent());
            attachment.setDelFlag("0");
            alertAttachmentMapper.insert(attachment);
        });
    }

    private void saveAlertDisposition(String alertId, String actionType, String actionResult, String note, int attachmentsCount) {
        PatrolAlertDisposition disposition = new PatrolAlertDisposition();
        disposition.setTenantId(TENANT_ID);
        disposition.setDispositionId("AD-" + UUID.randomUUID());
        disposition.setAlertId(alertId);
        disposition.setActionType(actionType);
        disposition.setActionResult(actionResult);
        disposition.setOperatorId(currentOperator());
        disposition.setOperatorName(currentOperator());
        disposition.setNote(note);
        disposition.setAttachmentsCount(attachmentsCount);
        disposition.setOccurredAt(new Date());
        disposition.setDelFlag("0");
        alertDispositionMapper.insert(disposition);
    }

    private void saveLocationTrack(PatrolDevice device, HeartbeatRequestDto request) {
        PatrolDeviceBinding binding = activeBinding(device.getDeviceId());
        PatrolLocationTrack track = new PatrolLocationTrack();
        track.setTenantId(TENANT_ID);
        track.setTrackId("TRK-" + UUID.randomUUID());
        track.setBadgeNo(binding == null ? LoginHelper.getUsername() : blankToDefault(binding.getBadgeNo(), binding.getUserName()));
        track.setOfficerName(binding == null ? LoginHelper.getUsername() : blankToDefault(binding.getNickName(), binding.getUserName()));
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
            return currentBoundDeviceId().equals(message.getTargetId());
        }
        return targetId.equals(message.getTargetId());
    }

    private PatrolMessageReceipt visibleReceipt(String messageId, String targetId) {
        List<String> keys = new ArrayList<>();
        String safeTargetId = blankToDefault(targetId, "");
        String username = blankToDefault(LoginHelper.getUsername(), "");
        String deviceId = currentBoundDeviceId();
        if (!safeTargetId.isBlank()) {
            keys.add(safeTargetId);
        }
        if (!username.isBlank() && !keys.contains(username)) {
            keys.add(username);
        }
        if (!deviceId.isBlank() && !keys.contains(deviceId)) {
            keys.add(deviceId);
        }
        return messageReceiptMapper.selectList(new LambdaQueryWrapper<PatrolMessageReceipt>()
                .eq(PatrolMessageReceipt::getMessageId, messageId))
            .stream()
            .filter(item -> keys.contains(item.getRecipientId()) || keys.contains(item.getDeviceId()))
            .findFirst()
            .orElse(null);
    }

    private void markReceiptDelivered(PatrolMessageReceipt receipt) {
        receipt.setLastPullAt(new Date());
        if (!"READ".equals(receipt.getDeliveryStatus())) {
            receipt.setDeliveryStatus("DELIVERED");
            if (receipt.getDeliveredAt() == null) {
                receipt.setDeliveredAt(new Date());
            }
        }
        messageReceiptMapper.updateById(receipt);
    }

    private PatrolMessage syncMessageDeliverySummary(String messageId) {
        PatrolMessage message = messageMapper.selectById(messageId);
        if (message == null) {
            return null;
        }
        List<PatrolMessageReceipt> receipts = messageReceiptMapper.selectList(new LambdaQueryWrapper<PatrolMessageReceipt>()
            .eq(PatrolMessageReceipt::getMessageId, messageId));
        if (receipts.isEmpty()) {
            return message;
        }
        int total = receipts.size();
        int read = Math.toIntExact(receipts.stream().filter(item -> "READ".equals(item.getDeliveryStatus())).count());
        int delivered = Math.toIntExact(receipts.stream().filter(item -> "DELIVERED".equals(item.getDeliveryStatus()) || "READ".equals(item.getDeliveryStatus())).count());
        message.setTotalCount(total);
        message.setReadCount(read);
        if (read >= total) {
            message.setStatus("READ");
        } else if (delivered >= total) {
            message.setStatus("DELIVERED");
        } else {
            message.setStatus("SENT");
        }
        messageMapper.updateById(message);
        return message;
    }

    private PatrolDeviceBinding activeBinding(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return null;
        }
        return deviceBindingMapper.selectList(new LambdaQueryWrapper<PatrolDeviceBinding>()
                .eq(PatrolDeviceBinding::getDeviceId, deviceId)
                .eq(PatrolDeviceBinding::getBindStatus, "BOUND")
                .orderByDesc(PatrolDeviceBinding::getBoundAt))
            .stream()
            .findFirst()
            .orElse(null);
    }

    private String currentBoundDeviceId() {
        String username = LoginHelper.getUsername();
        return deviceBindingMapper.selectList(new LambdaQueryWrapper<PatrolDeviceBinding>()
                .eq(PatrolDeviceBinding::getUserName, username)
                .eq(PatrolDeviceBinding::getBindStatus, "BOUND")
                .orderByDesc(PatrolDeviceBinding::getBoundAt))
            .stream()
            .findFirst()
            .map(PatrolDeviceBinding::getDeviceId)
            .orElse(DEVICE_ID);
    }

    private DeviceStatusDto toDeviceStatus(PatrolDevice device) {
        return new DeviceStatusDto(device.getDeviceId(), device.getDeviceName(), Boolean.TRUE.equals(device.getOnline()), value(device.getBatteryPercent(), 0), value(device.getSignalBars(), 0), blankToDefault(device.getOnlineDuration(), "00:00:00"), value(device.getStorageUsedGb(), 0F), value(device.getStorageTotalGb(), 0F), blankToDefault(device.getFirmwareVersion(), ""), blankToDefault(device.getRecordingStatus(), "IDLE"), Boolean.TRUE.equals(device.getTalking()), Boolean.TRUE.equals(device.getCloudConnected()));
    }

    private DeviceCapabilitiesDto toCapabilitiesDto(PatrolDeviceConfig config) {
        return new DeviceCapabilitiesDto(
            Boolean.TRUE.equals(config.getSupportsGlasses()),
            Boolean.TRUE.equals(config.getSupportsEarphone()),
            Boolean.TRUE.equals(config.getSupportsWifi()),
            Boolean.TRUE.equals(config.getSupportsFileTransfer()),
            Boolean.TRUE.equals(config.getSupportsPhoto()),
            Boolean.TRUE.equals(config.getSupportsVideo()),
            Boolean.TRUE.equals(config.getSupportsAudioRecord()),
            Boolean.TRUE.equals(config.getSupportsRealtimeAudio())
        );
    }

    private DeviceWifiStateDto toWifiDto(PatrolDeviceConfig config) {
        return new DeviceWifiStateDto(
            Boolean.TRUE.equals(config.getWifiEnabled()),
            blankToDefault(config.getWifiSsid(), ""),
            Boolean.TRUE.equals(config.getWifiPasswordConfigured()),
            Boolean.TRUE.equals(config.getWifiConnected())
        );
    }

    private DeviceAdvancedSettingsDto toSettingsDto(PatrolDeviceConfig config) {
        return new DeviceAdvancedSettingsDto(
            value(config.getVideoWidth(), 240),
            value(config.getVideoHeight(), 0),
            value(config.getVideoFrameRate(), 16),
            value(config.getRecordingDurationSeconds(), 24 * 60 * 60),
            Boolean.TRUE.equals(config.getVerticalRecording()),
            Boolean.TRUE.equals(config.getEnhancedSound()),
            value(config.getBrightnessLevel(), 2)
        );
    }

    private AlertDto toAlertDto(PatrolAlert alert) {
        return new AlertDto(alert.getAlertId(), alert.getTitle(), alert.getLevel(), alert.getStatus(), alert.getOccurredAt(), alert.getLocationText(), alert.getSource(), alert.getDescription(), alert.getConfidence());
    }

    private MediaFileDto toMediaDto(PatrolMedia media) {
        return new MediaFileDto(media.getFileId(), media.getFileName(), media.getMediaType(), media.getCapturedAt(), media.getSizeText(), media.getDurationText(), Boolean.TRUE.equals(media.getSha256Verified()), media.getStorageSide(), media.getTransferStatus(), value(media.getProgress(), 0F), media.getContentUri());
    }

    private MediaUploadTaskDto toUploadTaskDto(PatrolMediaUploadTask task) {
        return new MediaUploadTaskDto(
            task.getTaskId(),
            task.getFileId(),
            task.getFileName(),
            task.getMediaType(),
            task.getMimeType(),
            value(task.getFileSizeBytes(), 0L),
            value(task.getChunkSizeBytes(), DEFAULT_CHUNK_SIZE),
            value(task.getTotalChunks(), 0),
            value(task.getUploadedChunks(), 0),
            uploadedChunkIndexes(task),
            value(task.getUploadedBytes(), 0L),
            task.getExpectedSha256(),
            task.getActualSha256(),
            task.getStorageSide(),
            task.getBizType(),
            task.getBizId(),
            task.getStatus(),
            value(task.getProgress(), 0F),
            task.getErrorMessage(),
            task.getBadgeNo(),
            task.getOfficerName(),
            task.getDeviceId(),
            task.getCompletedAt() == null ? "" : task.getCompletedAt().toString()
        );
    }

    private SosEventDto toSosDto(PatrolSosEvent event) {
        GpsLocationDto location = event.getLatitude() == null ? null : new GpsLocationDto(event.getLatitude(), event.getLongitude(), value(event.getAccuracyMeters(), 0F), event.getAddress());
        return new SosEventDto(event.getSosId(), event.getPhase(), event.getMessage(), location, Boolean.TRUE.equals(event.getRecordingAudio()), event.getBackupEtaMinutes());
    }

    private PatrolMessageDto toMessageDto(PatrolMessage message, PatrolMessageReceipt receipt) {
        return new PatrolMessageDto(
            message.getMessageId(),
            message.getTitle(),
            message.getContent(),
            message.getTargetType(),
            receipt == null ? message.getStatus() : receipt.getDeliveryStatus(),
            receipt == null || receipt.getDeliveredAt() == null ? "" : receipt.getDeliveredAt().toString(),
            receipt == null || receipt.getReadAt() == null ? "" : receipt.getReadAt().toString(),
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

    private String currentOperator() {
        String username = LoginHelper.getUsername();
        return username == null || username.isBlank() ? "app" : username;
    }

    private String mediaType(String fileName, String contentType) {
        String lower = blankToDefault(fileName, "").toLowerCase();
        String type = blankToDefault(contentType, "").toLowerCase();
        if (type.startsWith("image/") || lower.matches(".*\\.(jpg|jpeg|png|webp|bmp)$")) {
            return "PHOTO";
        }
        if (type.startsWith("audio/") || lower.matches(".*\\.(mp3|wav|aac|m4a|amr|opus)$")) {
            return "AUDIO";
        }
        return "VIDEO";
    }

    private boolean verifyStoredMedia(PatrolMedia media) {
        if (media.getSha256() == null || media.getSha256().isBlank() || media.getOssId() == null) {
            return false;
        }
        String actual = sha256FromOss(media);
        return media.getSha256().equalsIgnoreCase(actual);
    }

    private void validateUploadTask(MediaUploadTaskCreateDto request) {
        if (request == null || request.getFileName() == null || request.getFileName().isBlank()) {
            throw new ServiceException("文件名不能为空");
        }
        if (request.getFileSizeBytes() <= 0) {
            throw new ServiceException("文件大小必须大于0");
        }
        long chunkSize = request.getChunkSizeBytes() > 0 ? request.getChunkSizeBytes() : DEFAULT_CHUNK_SIZE;
        int totalChunks = request.getTotalChunks() > 0 ? request.getTotalChunks() : (int) Math.ceil(request.getFileSizeBytes() * 1D / chunkSize);
        if (chunkSize <= 0 || totalChunks <= 0) {
            throw new ServiceException("分片参数不合法");
        }
    }

    private PatrolMediaUploadTask getUploadTask(String taskId) {
        PatrolMediaUploadTask task = mediaUploadTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("上传任务不存在");
        }
        return task;
    }

    private void refreshUploadProgress(PatrolMediaUploadTask task) {
        int uploadedChunks = 0;
        long uploadedBytes = 0L;
        Path dir = uploadTaskDir(task.getTaskId());
        for (int i = 0; i < task.getTotalChunks(); i++) {
            Path part = dir.resolve(i + ".part");
            if (Files.exists(part)) {
                uploadedChunks++;
                try {
                    uploadedBytes += Files.size(part);
                } catch (IOException e) {
                    throw new ServiceException("读取分片大小失败：" + e.getMessage());
                }
            }
        }
        task.setUploadedChunks(uploadedChunks);
        task.setUploadedBytes(uploadedBytes);
        task.setProgress(Math.min(uploadedChunks * 1F / Math.max(task.getTotalChunks(), 1), 0.98F));
    }

    private List<Integer> uploadedChunkIndexes(PatrolMediaUploadTask task) {
        List<Integer> indexes = new ArrayList<>();
        Path dir = uploadTaskDir(task.getTaskId());
        for (int i = 0; i < value(task.getTotalChunks(), 0); i++) {
            if (Files.exists(dir.resolve(i + ".part"))) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private File mergeUploadTask(PatrolMediaUploadTask task) {
        refreshUploadProgress(task);
        if (task.getUploadedChunks() < task.getTotalChunks()) {
            throw new ServiceException("分片尚未上传完成");
        }
        Path dir = uploadTaskDir(task.getTaskId());
        Path merged = dir.resolve("merged-" + safeFileName(task.getFileName()));
        try (OutputStream output = Files.newOutputStream(merged)) {
            for (int i = 0; i < task.getTotalChunks(); i++) {
                Path part = dir.resolve(i + ".part");
                if (!Files.exists(part)) {
                    throw new ServiceException("缺少分片：" + i);
                }
                Files.copy(part, output);
            }
        } catch (IOException e) {
            markUploadTaskFailed(task, "合并上传分片失败：" + e.getMessage());
            throw new ServiceException(task.getErrorMessage());
        }
        return merged.toFile();
    }

    private void markUploadTaskFailed(PatrolMediaUploadTask task, String message) {
        task.setStatus("FAILED");
        task.setErrorMessage(message);
        mediaUploadTaskMapper.updateById(task);
    }

    private Path uploadTaskDir(String taskId) {
        return Path.of(System.getProperty("java.io.tmpdir"), "patrollink-media-upload", taskId);
    }

    private void deleteQuietly(Path path) {
        if (path == null || !Files.exists(path)) {
            return;
        }
        try (var paths = Files.walk(path)) {
            paths.sorted(Comparator.reverseOrder()).forEach(item -> {
                try {
                    Files.deleteIfExists(item);
                } catch (IOException ignored) {
                    // 临时文件清理失败不影响媒体入库。
                }
            });
        } catch (IOException ignored) {
            // 临时文件清理失败不影响媒体入库。
        }
    }

    private String safeFileName(String fileName) {
        String normalized = blankToDefault(fileName, "media.bin").replace("\\", "/");
        return Path.of(normalized).getFileName().toString();
    }

    private String sha256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (DigestOutputStream output = new DigestOutputStream(OutputStream.nullOutputStream(), digest)) {
                file.getInputStream().transferTo(output);
            }
            return hex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new ServiceException("计算文件SHA-256失败：" + e.getMessage());
        }
    }

    private String sha256(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (DigestOutputStream output = new DigestOutputStream(OutputStream.nullOutputStream(), digest)) {
                Files.copy(file.toPath(), output);
            }
            return hex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new ServiceException("计算文件SHA-256失败：" + e.getMessage());
        }
    }

    private String sha256FromOss(PatrolMedia media) {
        try {
            SysOssVo oss = ossService.getById(media.getOssId());
            if (oss == null) {
                return "";
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (DigestOutputStream output = new DigestOutputStream(OutputStream.nullOutputStream(), digest)) {
                OssFactory.instance(oss.getService()).download(oss.getFileName(), output, null);
            }
            return hex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new ServiceException("校验媒体文件失败：" + e.getMessage());
        }
    }

    private String watermarkToken(String sha256, String badgeNo, String deviceId, String uploadedAt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String payload = sha256 + "|" + blankToDefault(badgeNo, "") + "|" + blankToDefault(deviceId, "") + "|" + blankToDefault(uploadedAt, "");
            return hex(digest.digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("生成证据水印令牌失败：" + e.getMessage());
        }
    }

    private String hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte item : bytes) {
            builder.append(String.format("%02x", item));
        }
        return builder.toString();
    }

    private String sizeText(long bytes) {
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024D);
        }
        return String.format("%.1f MB", bytes / 1024D / 1024D);
    }

    private List<String> changelog(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split("\\R"))
            .map(String::trim)
            .filter(item -> !item.isBlank())
            .toList();
    }

    private String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private int value(Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }

    private long value(Long value, long defaultValue) {
        return value == null ? defaultValue : value;
    }

    private float value(Float value, float defaultValue) {
        return value == null ? defaultValue : value;
    }

    private double value(Double value, double defaultValue) {
        return value == null ? defaultValue : value;
    }
}
