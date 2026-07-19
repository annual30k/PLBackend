package org.dromara.patrol.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.sse.core.SseEmitterManager;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.patrol.domain.PatrolAlert;
import org.dromara.patrol.domain.PatrolAlertAttachment;
import org.dromara.patrol.domain.PatrolAlertDisposition;
import org.dromara.patrol.domain.PatrolAppVersion;
import org.dromara.patrol.domain.PatrolArea;
import org.dromara.patrol.domain.PatrolAuditLog;
import org.dromara.patrol.domain.PatrolControlPerson;
import org.dromara.patrol.domain.PatrolControlVehicle;
import org.dromara.patrol.domain.PatrolDailyReport;
import org.dromara.patrol.domain.PatrolDevice;
import org.dromara.patrol.domain.PatrolDeviceBinding;
import org.dromara.patrol.domain.PatrolDeviceCommand;
import org.dromara.patrol.domain.PatrolDeviceConfig;
import org.dromara.patrol.domain.PatrolDeviceEvent;
import org.dromara.patrol.domain.PatrolFirmwareUpgradeTask;
import org.dromara.patrol.domain.PatrolFirmwareVersion;
import org.dromara.patrol.domain.PatrolLocationTrack;
import org.dromara.patrol.domain.PatrolMedia;
import org.dromara.patrol.domain.PatrolMediaUploadTask;
import org.dromara.patrol.domain.PatrolMessage;
import org.dromara.patrol.domain.PatrolMessageReceipt;
import org.dromara.patrol.domain.PatrolSosDisposition;
import org.dromara.patrol.domain.PatrolSosEvent;
import org.dromara.patrol.entity.DeviceAdvancedSettingsDto;
import org.dromara.patrol.entity.DeviceCapabilitiesDto;
import org.dromara.patrol.entity.DeviceControlResultDto;
import org.dromara.patrol.entity.DeviceWifiStateDto;
import org.dromara.patrol.entity.IntercomSessionDto;
import org.dromara.patrol.entity.IntercomSessionRequestDto;
import org.dromara.patrol.entity.IntercomSignalDto;
import org.dromara.patrol.entity.IntercomSignalRequestDto;
import org.dromara.patrol.entity.MediaUploadTaskDto;
import org.dromara.patrol.entity.PageEnvelope;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.dromara.patrol.mapper.PatrolAlertAttachmentMapper;
import org.dromara.patrol.mapper.PatrolAlertDispositionMapper;
import org.dromara.patrol.mapper.PatrolAlertMapper;
import org.dromara.patrol.mapper.PatrolAppVersionMapper;
import org.dromara.patrol.mapper.PatrolAreaMapper;
import org.dromara.patrol.mapper.PatrolAuditLogMapper;
import org.dromara.patrol.mapper.PatrolControlPersonMapper;
import org.dromara.patrol.mapper.PatrolControlVehicleMapper;
import org.dromara.patrol.mapper.PatrolDailyReportMapper;
import org.dromara.patrol.mapper.PatrolDeviceMapper;
import org.dromara.patrol.mapper.PatrolDeviceBindingMapper;
import org.dromara.patrol.mapper.PatrolDeviceCommandMapper;
import org.dromara.patrol.mapper.PatrolDeviceConfigMapper;
import org.dromara.patrol.mapper.PatrolDeviceEventMapper;
import org.dromara.patrol.mapper.PatrolFirmwareUpgradeTaskMapper;
import org.dromara.patrol.mapper.PatrolFirmwareVersionMapper;
import org.dromara.patrol.mapper.PatrolLocationTrackMapper;
import org.dromara.patrol.mapper.PatrolMediaMapper;
import org.dromara.patrol.mapper.PatrolMediaUploadTaskMapper;
import org.dromara.patrol.mapper.PatrolMessageMapper;
import org.dromara.patrol.mapper.PatrolMessageReceiptMapper;
import org.dromara.patrol.mapper.PatrolSosDispositionMapper;
import org.dromara.patrol.mapper.PatrolSosEventMapper;
import org.dromara.patrol.service.IPatrolAppService;
import org.dromara.patrol.service.PatrolRealtimePublisher;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.domain.SysOssConfig;
import org.dromara.system.mapper.SysOssConfigMapper;
import org.dromara.system.service.ISysOssService;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.sql.DataSource;

/**
 * PatrolLink command platform API.
 * <p>
 * Core device, alert, media, SOS, command, message and audit data are backed by
 * patrol tables. Real-time video relay is reserved until SDK capability is
 * available.
 */
@RestController
@RequestMapping("/patrol")
@RequiredArgsConstructor
public class PatrolController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final long DEVICE_HEARTBEAT_TIMEOUT_MILLIS = 45_000L;

    private final PatrolDeviceMapper deviceMapper;
    private final PatrolDeviceCommandMapper commandMapper;
    private final PatrolDeviceEventMapper deviceEventMapper;
    private final PatrolLocationTrackMapper locationTrackMapper;
    private final PatrolAlertMapper alertMapper;
    private final PatrolAlertAttachmentMapper alertAttachmentMapper;
    private final PatrolAlertDispositionMapper alertDispositionMapper;
    private final PatrolMediaMapper mediaMapper;
    private final PatrolMediaUploadTaskMapper mediaUploadTaskMapper;
    private final PatrolSosEventMapper sosEventMapper;
    private final PatrolMessageMapper messageMapper;
    private final PatrolMessageReceiptMapper messageReceiptMapper;
    private final PatrolAuditLogMapper auditLogMapper;
    private final PatrolSosDispositionMapper sosDispositionMapper;
    private final PatrolControlPersonMapper controlPersonMapper;
    private final PatrolControlVehicleMapper controlVehicleMapper;
    private final PatrolDailyReportMapper dailyReportMapper;
    private final PatrolDeviceBindingMapper deviceBindingMapper;
    private final PatrolAppVersionMapper appVersionMapper;
    private final PatrolAreaMapper areaMapper;
    private final PatrolFirmwareVersionMapper firmwareVersionMapper;
    private final PatrolFirmwareUpgradeTaskMapper firmwareUpgradeTaskMapper;
    private final PatrolDeviceConfigMapper deviceConfigMapper;
    private final IPatrolAppService patrolAppService;
    private final PatrolRealtimePublisher realtimePublisher;
    private final SseEmitterManager sseEmitterManager;
    private final ISysOssService ossService;
    private final SysOssConfigMapper ossConfigMapper;
    private final DataSource dataSource;
    private final RedissonConnectionFactory redisConnectionFactory;

    @GetMapping("/dashboard/summary")
    public R<DashboardSummaryVo> dashboard() {
        List<PatrolDevice> devices = deviceMapper.selectList();
        List<PatrolAlert> alerts = alertMapper.selectList();
        List<PatrolMedia> media = mediaMapper.selectList();
        List<PatrolSosEvent> sosEvents = sosEventMapper.selectList();
        long onlineDevices = devices.stream().filter(this::isDeviceOnline).count();
        long pendingAlerts = alerts.stream().filter(item -> !"CLOSED".equals(item.getStatus())).count();
        long activeSos = sosEvents.stream().filter(item -> "ACTIVE".equals(item.getPhase())).count();
        return R.ok(new DashboardSummaryVo(
            List.of(
                new MetricVo("在线警员", String.valueOf(onlineDevices), "按在线设备折算", "success"),
                new MetricVo("在线设备", String.valueOf(onlineDevices), devices.size() + " 台设备入库", "primary"),
                new MetricVo("视频会话", "预留", "SDK 暂未提供实时流能力", "warning"),
                new MetricVo("未处置预警", String.valueOf(pendingAlerts), "PENDING/HANDLING", pendingAlerts > 0 ? "danger" : "success"),
                new MetricVo("SOS 求助", String.valueOf(activeSos), "ACTIVE 状态", activeSos > 0 ? "danger" : "success"),
                new MetricVo("媒体证据", String.valueOf(media.size()), "MinIO/设备侧记录", "info")
            ),
            workItems(alerts, sosEvents, devices),
            new PlatformCapacityVo(Math.toIntExact(onlineDevices), devices.size(), 16, "实时视频待 SDK 能力开放", "高德地图", "第三方人脸比对/车牌 OCR")
        ));
    }

    @GetMapping("/devices")
    public R<PageEnvelope<DeviceVo>> devices(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(deviceMapper, new LambdaQueryWrapper<PatrolDevice>()
            .orderByDesc(PatrolDevice::getLastHeartbeatTime), page, pageSize, this::toDeviceVo));
    }

    @GetMapping("/devices/configs")
    public R<PageEnvelope<DeviceConfigVo>> deviceConfigs(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(deviceMapper, new LambdaQueryWrapper<PatrolDevice>()
            .orderByDesc(PatrolDevice::getLastHeartbeatTime), page, pageSize, this::toDeviceConfigVo));
    }

    @GetMapping("/devices/{deviceId}/config")
    public R<DeviceConfigVo> deviceConfig(@PathVariable String deviceId) {
        PatrolDevice device = deviceMapper.selectById(deviceId);
        return R.ok(toDeviceConfigVo(device == null ? fallbackDevice(deviceId) : device));
    }

    @PostMapping("/devices/{deviceId}/wifi")
    public R<DeviceConfigVo> configureDeviceWifi(@PathVariable String deviceId, @RequestBody DeviceWifiStateDto request) {
        patrolAppService.configureWifi(deviceId, request);
        logAudit("DEVICE", "后台配置设备Wi-Fi", deviceId, "SUCCESS");
        return R.ok(toDeviceConfigVo(deviceOrFallback(deviceId)));
    }

    @PostMapping("/devices/{deviceId}/settings")
    public R<DeviceConfigVo> applyDeviceSettings(@PathVariable String deviceId, @RequestBody DeviceAdvancedSettingsDto request) {
        patrolAppService.applySettings(deviceId, request);
        logAudit("DEVICE", "后台配置设备高级参数", deviceId, "SUCCESS");
        return R.ok(toDeviceConfigVo(deviceOrFallback(deviceId)));
    }

    @PostMapping("/devices/{deviceId}/realtime-audio/start")
    public R<DeviceControlResultDto> startDeviceRealtimeAudio(@PathVariable String deviceId) {
        DeviceControlResultDto result = patrolAppService.startRealtimeAudioSync(deviceId);
        logAudit("DEVICE", "请求开启硬件实时音频", deviceId, result.isSuccess() ? "SUCCESS" : "UNSUPPORTED");
        return R.ok(result);
    }

    @PostMapping("/devices/{deviceId}/realtime-audio/stop")
    public R<DeviceControlResultDto> stopDeviceRealtimeAudio(@PathVariable String deviceId) {
        DeviceControlResultDto result = patrolAppService.stopRealtimeAudioSync(deviceId);
        logAudit("DEVICE", "请求停止硬件实时音频", deviceId, result.isSuccess() ? "SUCCESS" : "UNSUPPORTED");
        return R.ok(result);
    }

    @PostMapping("/devices/{deviceId}/media-sync/completed")
    public R<DeviceControlResultDto> markDeviceMediaSyncCompleted(@PathVariable String deviceId) {
        DeviceControlResultDto result = patrolAppService.notifyMediaSyncCompleted(deviceId);
        logAudit("DEVICE", "后台标记媒体同步完成", deviceId, result.isSuccess() ? "SUCCESS" : "FAILED");
        return R.ok(result);
    }

    @PostMapping("/devices/{deviceId}/commands")
    public R<CommandResultVo> command(@PathVariable String deviceId, @RequestBody DeviceCommandBo command) {
        String commandId = "CMD-" + UUID.randomUUID();
        Date now = new Date();
        PatrolDeviceCommand record = new PatrolDeviceCommand();
        record.setCommandId(commandId);
        record.setTenantId(currentTenantId());
        record.setDeviceId(deviceId);
        record.setCommand(command.command());
        record.setOperatorId(blankToDefault(command.operatorId(), currentOperator()));
        record.setRequestId(command.requestId());
        record.setStatus("QUEUED");
        record.setResultMessage("指令已进入下行队列，等待 Android 拉取和设备真实回执");
        record.setSentAt(now);
        record.setDelFlag("0");
        commandMapper.insert(record);
        saveDeviceEvent(deviceId, "COMMAND", "INFO", "平台下发设备指令", command.command());
        logAudit("COMMAND", "下发设备指令：" + command.command(), deviceId, "SUCCESS");
        realtimePublisher.publish("DEVICE_COMMAND", "devices", "平台下发设备指令", deviceId + " " + command.command(), deviceId,
            realtimePublisher.payload("commandId", commandId, "deviceId", deviceId, "command", command.command(), "status", "QUEUED"));
        return R.ok(new CommandResultVo(commandId, deviceId, command.command(), "QUEUED", record.getResultMessage()));
    }

    @GetMapping("/devices/commands")
    public R<PageEnvelope<CommandLogVo>> deviceCommands(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(commandMapper, new LambdaQueryWrapper<PatrolDeviceCommand>()
            .orderByDesc(PatrolDeviceCommand::getSentAt), page, pageSize, this::toCommandLogVo));
    }

    @GetMapping("/devices/{deviceId}/commands")
    public R<List<CommandLogVo>> deviceCommands(@PathVariable String deviceId) {
        return R.ok(commandMapper.selectList(new LambdaQueryWrapper<PatrolDeviceCommand>()
                .eq(PatrolDeviceCommand::getDeviceId, deviceId)
                .orderByDesc(PatrolDeviceCommand::getSentAt))
            .stream()
            .limit(20)
            .map(this::toCommandLogVo)
            .toList());
    }

    @GetMapping("/devices/events")
    public R<PageEnvelope<DeviceEventVo>> deviceEvents(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(deviceEventMapper, new LambdaQueryWrapper<PatrolDeviceEvent>()
            .orderByDesc(PatrolDeviceEvent::getOccurredAt), page, pageSize, this::toDeviceEventVo));
    }

    @GetMapping("/devices/{deviceId}/events")
    public R<List<DeviceEventVo>> deviceEvents(@PathVariable String deviceId) {
        return R.ok(deviceEventMapper.selectList(new LambdaQueryWrapper<PatrolDeviceEvent>()
                .eq(PatrolDeviceEvent::getDeviceId, deviceId)
                .orderByDesc(PatrolDeviceEvent::getOccurredAt))
            .stream()
            .limit(50)
            .map(this::toDeviceEventVo)
            .toList());
    }

    @GetMapping("/dispatch/channels")
    public R<List<DispatchChannelVo>> dispatchChannels() {
        return R.ok(deviceMapper.selectList(new LambdaQueryWrapper<PatrolDevice>().eq(PatrolDevice::getOnline, true)).stream()
            .filter(this::isDeviceOnline)
            .map(item -> new DispatchChannelVo("CH-" + item.getDeviceId(), item.getDeviceId(), officerName(item), deptName(item), Boolean.TRUE.equals(item.getTalking()) ? "INTERCOM" : "READY", "WebRTC/VoIP", 0, blankToDefault(item.getAddress(), "未知位置"), Boolean.TRUE.equals(item.getTalking())))
            .toList());
    }

    @PostMapping("/dispatch/intercom/sessions")
    public R<IntercomSessionDto> createIntercomSession(@RequestBody IntercomSessionRequestDto request) {
        return R.ok(patrolAppService.createIntercomSession(request));
    }

    @PostMapping("/dispatch/intercom/sessions/{sessionId}/close")
    public R<IntercomSessionDto> closeIntercomSession(@PathVariable String sessionId) {
        return R.ok(patrolAppService.closeIntercomSession(sessionId));
    }

    @PostMapping("/dispatch/intercom/sessions/{sessionId}/signals")
    public R<IntercomSignalDto> sendIntercomSignal(@PathVariable String sessionId, @RequestBody IntercomSignalRequestDto request) {
        return R.ok(patrolAppService.sendIntercomSignal(sessionId, request));
    }

    @GetMapping("/dispatch/intercom/sessions/{sessionId}/signals")
    public R<List<IntercomSignalDto>> intercomSignals(@PathVariable String sessionId, @RequestParam(defaultValue = "") String afterSignalId) {
        return R.ok(patrolAppService.intercomSignals(sessionId, afterSignalId));
    }

    @GetMapping("/map/officers")
    public R<List<OfficerLocationVo>> officerLocations() {
        return R.ok(deviceMapper.selectList().stream()
            .filter(item -> item.getLatitude() != null && item.getLongitude() != null)
            .map(item -> new OfficerLocationVo(
                badgeNo(item),
                officerName(item),
                deptName(item),
                item.getDeviceId(),
                BigDecimal.valueOf(item.getLatitude()),
                BigDecimal.valueOf(item.getLongitude()),
                blankToDefault(item.getAddress(), "未知位置"),
                isDeviceOnline(item) ? "ONLINE" : "OFFLINE",
                value(item.getBatteryPercent()),
                formatDate(item.getLastHeartbeatTime())
            ))
            .toList());
    }

    @GetMapping("/map/officers/{badgeNo}/track")
    public R<List<TrackPointVo>> officerTrack(@PathVariable String badgeNo) {
        return R.ok(locationTrackMapper.selectList(new LambdaQueryWrapper<PatrolLocationTrack>()
                .eq(PatrolLocationTrack::getBadgeNo, badgeNo)
                .orderByDesc(PatrolLocationTrack::getReportedAt))
            .stream()
            .limit(100)
            .map(this::toTrackPointVo)
            .toList());
    }

    @GetMapping("/areas")
    public R<List<PatrolAreaVo>> patrolAreas() {
        return R.ok(areaMapper.selectList(new LambdaQueryWrapper<PatrolArea>()
                .orderByDesc(PatrolArea::getUpdateTime)
                .orderByDesc(PatrolArea::getCreateTime))
            .stream()
            .map(this::toPatrolAreaVo)
            .toList());
    }

    @GetMapping("/areas/current")
    public R<PatrolAreaVo> currentPatrolAreaForConsole() {
        return R.ok(toPatrolAreaVo(currentArea()));
    }

    @PostMapping("/areas")
    public R<PatrolAreaVo> savePatrolArea(@RequestBody PatrolAreaBo request) {
        PatrolArea area = request.areaId() == null || request.areaId().isBlank()
            ? null
            : areaMapper.selectById(request.areaId());
        if (area == null) {
            area = new PatrolArea();
            area.setAreaId(blankToDefault(request.areaId(), "AREA-" + IdUtil.fastSimpleUUID()));
            area.setTenantId(currentTenantId());
            area.setDelFlag("0");
            area.setCreateTime(new Date());
        }
        area.setAreaName(blankToDefault(request.areaName(), "未命名执勤辖区"));
        area.setTeamId(blankToDefault(request.teamId(), "DEFAULT"));
        area.setTeamName(blankToDefault(request.teamName(), "默认执勤组"));
        area.setOwnerType(blankToDefault(request.ownerType(), "TEAM"));
        area.setUserId(parseLongOrNull(request.userId()));
        area.setBadgeNo(blankToDefault(request.badgeNo(), ""));
        area.setBoundaryJson(JsonUtils.toJsonString(request.boundary() == null ? List.of() : request.boundary()));
        area.setRouteJson(JsonUtils.toJsonString(request.route() == null ? List.of() : request.route()));
        area.setUpdateTime(new Date());
        areaMapper.insertOrUpdate(area);
        logAudit("AREA", "保存执勤辖区", area.getAreaId(), "SUCCESS");
        realtimePublisher.publish("PATROL_AREA_UPDATED", "map", "执勤辖区已更新", area.getAreaName(), area.getAreaId(),
            realtimePublisher.payload("areaId", area.getAreaId(), "areaName", area.getAreaName(), "teamName", area.getTeamName()));
        return R.ok(toPatrolAreaVo(area));
    }

    @GetMapping("/alerts")
    public R<PageEnvelope<AlertVo>> alerts(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(alertMapper, new LambdaQueryWrapper<PatrolAlert>()
            .orderByDesc(PatrolAlert::getOccurredAt), page, pageSize, this::toAlertVo));
    }

    @PostMapping("/alerts/{alertId}/ack")
    public R<AlertActionVo> acknowledgeAlert(@PathVariable String alertId) {
        PatrolAlert alert = alertMapper.selectById(alertId);
        if (alert != null) {
            alert.setStatus("HANDLING");
            alertMapper.updateById(alert);
        }
        saveAlertDisposition(alertId, "ACK", "HANDLING", null, 0);
        logAudit("ALERT", "确认预警", alertId, "SUCCESS");
        realtimePublisher.publish("ALERT_UPDATED", "alerts", "预警已确认", alertId + " 已进入处置中", alertId,
            realtimePublisher.payload("alertId", alertId, "status", "HANDLING"));
        return R.ok(new AlertActionVo(alertId, "HANDLING", "预警已确认，进入处置中"));
    }

    @PostMapping("/alerts/{alertId}/close")
    public R<AlertActionVo> closeAlert(@PathVariable String alertId, @RequestBody AlertCloseBo bo) {
        PatrolAlert alert = alertMapper.selectById(alertId);
        if (alert != null) {
            alert.setStatus("CLOSED");
            alert.setCloseResult(bo.result());
            alert.setCloseNote(bo.note());
            alertMapper.updateById(alert);
        }
        saveAlertAttachments(alertId, bo.attachments());
        saveAlertDisposition(alertId, "CLOSE", bo.result(), bo.note(), bo.attachments() == null ? 0 : bo.attachments().size());
        logAudit("ALERT", "关闭预警：" + bo.result(), alertId, "SUCCESS");
        realtimePublisher.publish("ALERT_UPDATED", "alerts", "预警已关闭", alertId + " 处置结果：" + bo.result(), alertId,
            realtimePublisher.payload("alertId", alertId, "status", "CLOSED", "result", bo.result()));
        return R.ok(new AlertActionVo(alertId, "CLOSED", "处置结果已提交：" + bo.result()));
    }

    @GetMapping("/alerts/{alertId}/attachments")
    public R<List<AlertAttachmentVo>> alertAttachments(@PathVariable String alertId) {
        return R.ok(alertAttachmentMapper.selectList(new LambdaQueryWrapper<PatrolAlertAttachment>()
                .eq(PatrolAlertAttachment::getAlertId, alertId)
                .orderByDesc(PatrolAlertAttachment::getCreateTime))
            .stream()
            .map(this::toAlertAttachmentVo)
            .toList());
    }

    @GetMapping("/alerts/{alertId}/dispositions")
    public R<List<AlertDispositionVo>> alertDispositions(@PathVariable String alertId) {
        return R.ok(alertDispositionMapper.selectList(new LambdaQueryWrapper<PatrolAlertDisposition>()
                .eq(PatrolAlertDisposition::getAlertId, alertId)
                .orderByDesc(PatrolAlertDisposition::getOccurredAt))
            .stream()
            .map(this::toAlertDispositionVo)
            .toList());
    }

    @GetMapping("/media")
    public R<PageEnvelope<MediaVo>> media(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(mediaMapper, new LambdaQueryWrapper<PatrolMedia>()
            .orderByDesc(PatrolMedia::getCapturedAt)
            .orderByDesc(PatrolMedia::getCreateTime), page, pageSize, this::toMediaVo));
    }

    @GetMapping("/media/upload-tasks")
    public R<PageEnvelope<MediaUploadTaskDto>> mediaUploadTasks(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(patrolAppService.mediaUploadTasks(normalizePage(page), normalizePageSize(pageSize)));
    }

    @PostMapping("/media/upload-tasks/cleanup")
    public R<CleanupResultVo> cleanMediaUploadTasks() {
        Integer cleaned = patrolAppService.cleanExpiredMediaUploadTasks(24);
        return R.ok(new CleanupResultVo(cleaned, "已清理过期分片上传任务"));
    }

    @PostMapping("/media/{fileId}/verify")
    public R<MediaActionVo> verifyMedia(@PathVariable String fileId) {
        boolean verified = patrolAppService.verifyMedia(fileId);
        logAudit("MEDIA", "校验媒体证据", fileId, verified ? "SUCCESS" : "FAILED");
        return R.ok(new MediaActionVo(fileId, verified ? "VERIFIED" : "FAILED", verified ? "证据完整性校验通过" : "媒体文件不存在或哈希不匹配"));
    }

    @DeleteMapping("/media/{fileId}")
    public R<MediaActionVo> deleteMedia(@PathVariable String fileId) {
        int deleted = mediaMapper.delete(new LambdaQueryWrapper<PatrolMedia>().eq(PatrolMedia::getFileId, fileId));
        logAudit("MEDIA", "删除媒体证据", fileId, deleted > 0 ? "SUCCESS" : "FAILED");
        realtimePublisher.publish("MEDIA_DELETED", "media", deleted > 0 ? "媒体证据已删除" : "媒体删除失败", fileId, fileId,
            realtimePublisher.payload("fileId", fileId, "deleted", deleted > 0));
        return R.ok(new MediaActionVo(fileId, deleted > 0 ? "DELETED" : "FAILED", deleted > 0 ? "媒体文件已删除" : "媒体文件不存在"));
    }

    @GetMapping("/daily-reports")
    public R<PageEnvelope<DailyReportVo>> dailyReports(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(defaultValue = "") String missionId,
        @RequestParam(defaultValue = "") String status) {
        LambdaQueryWrapper<PatrolDailyReport> wrapper = new LambdaQueryWrapper<PatrolDailyReport>()
            .like(missionId != null && !missionId.isBlank(), PatrolDailyReport::getMissionId, missionId)
            .eq(status != null && !status.isBlank(), PatrolDailyReport::getStatus, status)
            .orderByDesc(PatrolDailyReport::getGeneratedAt);
        return R.ok(pageOf(dailyReportMapper, wrapper, page, pageSize, this::toDailyReportVo));
    }

    @GetMapping("/daily-reports/{reportId}")
    public R<DailyReportVo> dailyReport(@PathVariable String reportId) {
        return R.ok(toDailyReportVo(dailyReportMapper.selectById(reportId)));
    }

    @PatchMapping("/daily-reports/{reportId}/status")
    public R<DailyReportVo> updateDailyReportStatus(@PathVariable String reportId, @RequestBody StatusBo bo) {
        PatrolDailyReport report = dailyReportMapper.selectById(reportId);
        if (report == null) {
            throw new ServiceException("日报不存在");
        }
        report.setStatus(blankToDefault(bo.status(), "REVIEWED"));
        dailyReportMapper.updateById(report);
        logAudit("DAILY_REPORT", "更新日报状态：" + report.getStatus(), reportId, "SUCCESS");
        realtimePublisher.publish("DAILY_REPORT_UPDATED", "reports", "日报状态已更新", reportId + " " + report.getStatus(), reportId,
            realtimePublisher.payload("reportId", reportId, "status", report.getStatus()));
        return R.ok(toDailyReportVo(report));
    }

    @PatchMapping("/daily-reports/{reportId}/content")
    public R<DailyReportVo> updateDailyReportContent(@PathVariable String reportId, @RequestBody DailyReportContentBo bo) {
        PatrolDailyReport report = dailyReportMapper.selectById(reportId);
        if (report == null) {
            throw new ServiceException("日报不存在");
        }
        report.setContent(blankToDefault(bo.content(), ""));
        dailyReportMapper.updateById(report);
        logAudit("DAILY_REPORT", "更新日报正文", reportId, "SUCCESS");
        realtimePublisher.publish("DAILY_REPORT_UPDATED", "reports", "日报正文已更新", reportId, reportId,
            realtimePublisher.payload("reportId", reportId, "contentUpdated", true));
        return R.ok(toDailyReportVo(report));
    }

    @GetMapping("/versions")
    public R<PageEnvelope<AppVersionVo>> versions(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(appVersionMapper, new LambdaQueryWrapper<PatrolAppVersion>()
            .orderByDesc(PatrolAppVersion::getVersionCode), page, pageSize, this::toAppVersionVo));
    }

    @PostMapping(value = "/versions/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<AppVersionPackageVo> uploadVersionPackage(@RequestPart("file") MultipartFile file) {
        String fileName = blankToDefault(file.getOriginalFilename(), "PatrolLink.apk");
        if (!fileName.toLowerCase().endsWith(".apk")) {
            throw new ServiceException("仅支持上传 APK 安装包");
        }
        String sha256 = sha256(file);
        SysOssVo oss = ossService.upload(file);
        String fileId = "FILE-" + oss.getOssId();
        String downloadUrl = "/files/" + fileId + "/download";
        PatrolMedia media = new PatrolMedia();
        media.setMediaId(IdUtil.getSnowflakeNextId());
        media.setTenantId(currentTenantId());
        media.setFileId(fileId);
        media.setFileName(fileName);
        media.setMediaType("APP");
        media.setCapturedAt(formatDate(new Date()));
        media.setSizeText(sizeText(file.getSize()));
        media.setFileSizeBytes(file.getSize());
        media.setMimeType(blankToDefault(file.getContentType(), "application/vnd.android.package-archive"));
        media.setSha256Verified(true);
        media.setStorageSide("APP_VERSION");
        media.setTransferStatus("DONE");
        media.setProgress(1F);
        media.setContentUri(downloadUrl);
        media.setOssId(oss.getOssId());
        media.setBucketName("ruoyi");
        media.setObjectKey(oss.getFileName());
        media.setSha256(sha256);
        media.setBizType("APP_VERSION");
        media.setBizId(fileId);
        media.setEvidenceSource("VERSION_PACKAGE");
        media.setDelFlag("0");
        mediaMapper.insert(media);
        logAudit("VERSION", "上传App安装包", fileName, "SUCCESS");
        return R.ok(new AppVersionPackageVo(fileId, fileName, downloadUrl, sha256, file.getSize(), sizeText(file.getSize())));
    }

    @PostMapping("/versions")
    public R<AppVersionVo> createVersion(@RequestBody AppVersionBo bo) {
        PatrolMedia packageFile = bo.fileId() == null || bo.fileId().isBlank() ? null : mediaMapper.selectOne(new LambdaQueryWrapper<PatrolMedia>()
            .eq(PatrolMedia::getFileId, bo.fileId())
            .eq(PatrolMedia::getStorageSide, "APP_VERSION")
            .orderByDesc(PatrolMedia::getCreateTime)
            .last("limit 1"));
        PatrolAppVersion version = new PatrolAppVersion();
        version.setVersionId("VER-" + UUID.randomUUID());
        version.setTenantId(currentTenantId());
        version.setVersionCode(bo.versionCode());
        version.setVersionName(blankToDefault(bo.versionName(), "1.0.0"));
        version.setForceUpdate(Boolean.TRUE.equals(bo.forceUpdate()));
        version.setChangelog(bo.changelog());
        version.setDownloadUrl(blankToDefault(bo.downloadUrl(), packageFile == null ? "" : blankToDefault(packageFile.getContentUri(), "/files/" + packageFile.getFileId() + "/download")));
        version.setSha256(blankToDefault(bo.sha256(), packageFile == null ? "" : packageFile.getSha256()));
        version.setFileId(bo.fileId());
        version.setStatus("PUBLISHED");
        version.setPublishedAt(new Date());
        version.setDelFlag("0");
        appVersionMapper.insert(version);
        logAudit("VERSION", "新增App版本", version.getVersionName(), "SUCCESS");
        return R.ok(toAppVersionVo(version));
    }

    @PatchMapping("/versions/{versionId}/status")
    public R<AppVersionVo> updateVersionStatus(@PathVariable String versionId, @RequestBody StatusBo bo) {
        PatrolAppVersion version = appVersionMapper.selectById(versionId);
        if (version != null) {
            version.setStatus(bo.status());
            if ("PUBLISHED".equals(bo.status())) {
                version.setPublishedAt(new Date());
            }
            appVersionMapper.updateById(version);
            logAudit("VERSION", "更新App版本状态：" + bo.status(), versionId, "SUCCESS");
        }
        return R.ok(version == null ? null : toAppVersionVo(version));
    }

    @GetMapping("/firmware-versions")
    public R<PageEnvelope<FirmwareVersionVo>> firmwareVersions(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(firmwareVersionMapper, new LambdaQueryWrapper<PatrolFirmwareVersion>()
            .orderByDesc(PatrolFirmwareVersion::getVersionCode), page, pageSize, this::toFirmwareVersionVo));
    }

    @PostMapping(value = "/firmware-versions/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<FirmwarePackageVo> uploadFirmwarePackage(@RequestPart("file") MultipartFile file) {
        String fileName = blankToDefault(file.getOriginalFilename(), "firmware.bin");
        String lower = fileName.toLowerCase();
        if (!lower.endsWith(".bin") && !lower.endsWith(".zip") && !lower.endsWith(".ufw")) {
            throw new ServiceException("仅支持上传 bin/zip/ufw 固件包");
        }
        String sha256 = sha256(file);
        SysOssVo oss = ossService.upload(file);
        String fileId = "FILE-" + oss.getOssId();
        String downloadUrl = "/files/" + fileId + "/download";
        PatrolMedia media = new PatrolMedia();
        media.setMediaId(IdUtil.getSnowflakeNextId());
        media.setTenantId(currentTenantId());
        media.setFileId(fileId);
        media.setFileName(fileName);
        media.setMediaType("FIRMWARE");
        media.setCapturedAt(formatDate(new Date()));
        media.setSizeText(sizeText(file.getSize()));
        media.setFileSizeBytes(file.getSize());
        media.setMimeType(blankToDefault(file.getContentType(), "application/octet-stream"));
        media.setSha256Verified(true);
        media.setStorageSide("FIRMWARE_VERSION");
        media.setTransferStatus("DONE");
        media.setProgress(1F);
        media.setContentUri(downloadUrl);
        media.setOssId(oss.getOssId());
        media.setBucketName("ruoyi");
        media.setObjectKey(oss.getFileName());
        media.setSha256(sha256);
        media.setBizType("FIRMWARE_VERSION");
        media.setBizId(fileId);
        media.setEvidenceSource("FIRMWARE_PACKAGE");
        media.setDelFlag("0");
        mediaMapper.insert(media);
        logAudit("FIRMWARE", "上传设备固件包", fileName, "SUCCESS");
        return R.ok(new FirmwarePackageVo(fileId, fileName, downloadUrl, sha256, file.getSize(), sizeText(file.getSize()), packageFormat(fileName)));
    }

    @PostMapping("/firmware-versions")
    public R<FirmwareVersionVo> createFirmwareVersion(@RequestBody FirmwareVersionBo bo) {
        PatrolMedia packageFile = bo.fileId() == null || bo.fileId().isBlank() ? null : mediaMapper.selectOne(new LambdaQueryWrapper<PatrolMedia>()
            .eq(PatrolMedia::getFileId, bo.fileId())
            .eq(PatrolMedia::getStorageSide, "FIRMWARE_VERSION")
            .orderByDesc(PatrolMedia::getCreateTime)
            .last("limit 1"));
        PatrolFirmwareVersion firmware = new PatrolFirmwareVersion();
        firmware.setFirmwareId("FW-" + UUID.randomUUID());
        firmware.setTenantId(currentTenantId());
        firmware.setDeviceType(blankToDefault(bo.deviceType(), "HEADSET"));
        firmware.setVendor(blankToDefault(bo.vendor(), ""));
        firmware.setChipset(blankToDefault(bo.chipset(), ""));
        firmware.setDeviceModel(blankToDefault(bo.deviceModel(), ""));
        firmware.setHardwareVersion(blankToDefault(bo.hardwareVersion(), ""));
        firmware.setFirmwareType(blankToDefault(bo.firmwareType(), "FULL"));
        firmware.setVersionCode(value(bo.versionCode(), 1));
        firmware.setVersionName(blankToDefault(bo.versionName(), ""));
        firmware.setMinCurrentVersion(blankToDefault(bo.minCurrentVersion(), ""));
        firmware.setMaxCurrentVersion(blankToDefault(bo.maxCurrentVersion(), ""));
        firmware.setForceUpdate(Boolean.TRUE.equals(bo.forceUpdate()));
        firmware.setChangelog(blankToDefault(bo.changelog(), ""));
        firmware.setDownloadUrl(blankToDefault(bo.downloadUrl(), packageFile == null ? "" : blankToDefault(packageFile.getContentUri(), "/files/" + packageFile.getFileId() + "/download")));
        firmware.setSha256(blankToDefault(bo.sha256(), packageFile == null ? "" : packageFile.getSha256()));
        firmware.setFileId(bo.fileId());
        firmware.setFileSizeBytes(packageFile == null ? bo.fileSizeBytes() : packageFile.getFileSizeBytes());
        firmware.setPackageFormat(blankToDefault(bo.packageFormat(), packageFile == null ? "" : packageFormat(packageFile.getFileName())));
        firmware.setUpgradeMode(blankToDefault(bo.upgradeMode(), "APP_BLE"));
        firmware.setGrayScope(blankToDefault(bo.grayScope(), "ALL"));
        firmware.setGrayTargets(blankToDefault(bo.grayTargets(), ""));
        firmware.setStatus("PUBLISHED");
        firmware.setPublishedAt(new Date());
        firmware.setRemark(blankToDefault(bo.remark(), ""));
        firmware.setDelFlag("0");
        firmwareVersionMapper.insert(firmware);
        logAudit("FIRMWARE", "新增设备固件版本", firmware.getVersionName(), "SUCCESS");
        return R.ok(toFirmwareVersionVo(firmware));
    }

    @PatchMapping("/firmware-versions/{firmwareId}/status")
    public R<FirmwareVersionVo> updateFirmwareVersionStatus(@PathVariable String firmwareId, @RequestBody StatusBo bo) {
        PatrolFirmwareVersion firmware = firmwareVersionMapper.selectById(firmwareId);
        if (firmware != null) {
            firmware.setStatus(bo.status());
            if ("PUBLISHED".equals(bo.status())) {
                firmware.setPublishedAt(new Date());
            }
            firmwareVersionMapper.updateById(firmware);
            logAudit("FIRMWARE", "更新设备固件状态：" + bo.status(), firmwareId, "SUCCESS");
        }
        return R.ok(firmware == null ? null : toFirmwareVersionVo(firmware));
    }

    @GetMapping("/firmware-upgrade-tasks")
    public R<PageEnvelope<FirmwareUpgradeTaskVo>> firmwareUpgradeTasks(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(firmwareUpgradeTaskMapper, new LambdaQueryWrapper<PatrolFirmwareUpgradeTask>()
            .orderByDesc(PatrolFirmwareUpgradeTask::getStartedAt), page, pageSize, this::toFirmwareUpgradeTaskVo));
    }

    @GetMapping("/sos")
    public R<PageEnvelope<SosVo>> sos(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(sosEventMapper, new LambdaQueryWrapper<PatrolSosEvent>()
            .orderByDesc(PatrolSosEvent::getCreateTime), page, pageSize, this::toSosVo));
    }

    @GetMapping("/sos/{sosId}/timeline")
    public R<List<SosTimelineVo>> sosTimeline(@PathVariable String sosId) {
        PatrolSosEvent event = sosEventMapper.selectById(sosId);
        List<SosTimelineVo> timeline = new ArrayList<>();
        if (event != null) {
            timeline.add(new SosTimelineVo(
                "SOS-ACTIVATE-" + event.getSosId(),
                event.getSosId(),
                "ACTIVATE",
                event.getPhase(),
                "App端",
                blankToDefault(event.getMessage(), "SOS求助已激活"),
                null,
                null,
                null,
                null,
                event.getBackupEtaMinutes(),
                formatDate(event.getCreateTime())
            ));
        }
        sosDispositionMapper.selectList(new LambdaQueryWrapper<PatrolSosDisposition>()
                .eq(PatrolSosDisposition::getSosId, sosId)
                .orderByAsc(PatrolSosDisposition::getOccurredAt))
            .stream()
            .map(this::toSosTimelineVo)
            .forEach(timeline::add);
        mediaMapper.selectList(new LambdaQueryWrapper<PatrolMedia>()
                .eq(PatrolMedia::getBizType, "SOS_AUDIO")
                .eq(PatrolMedia::getBizId, sosId)
                .orderByAsc(PatrolMedia::getCreateTime))
            .stream()
            .map(media -> new SosTimelineVo(
                "SOS-MEDIA-" + media.getFileId(),
                sosId,
                "SOS_AUDIO_UPLOADED",
                "ATTACHED",
                blankToDefault(media.getOfficerName(), "App端"),
                "移动端现场录音已上传并完成哈希校验",
                null,
                null,
                media.getFileId(),
                media.getFileName(),
                null,
                formatDate(media.getCreateTime())
            ))
            .forEach(timeline::add);
        timeline.sort((left, right) -> left.occurredAt().compareTo(right.occurredAt()));
        return R.ok(timeline);
    }

    @PostMapping("/sos/{sosId}/backup")
    public R<SosActionVo> assignSosBackup(@PathVariable String sosId, @RequestBody SosBackupBo bo) {
        PatrolSosEvent event = sosEventMapper.selectById(sosId);
        if (event == null) {
            return R.ok(new SosActionVo(sosId, "FAILED", "SOS事件不存在"));
        }
        event.setBackupEtaMinutes(bo.backupEtaMinutes());
        event.setMessage(blankToDefault(bo.note(), "已指派增援：" + blankToDefault(bo.contactName(), "增援警力")));
        sosEventMapper.updateById(event);
        saveSosDisposition(sosId, "ASSIGN_BACKUP", "ASSIGNED", event.getMessage(), bo.contactName(), bo.contactPhone(), null, null, bo.backupEtaMinutes());
        logAudit("SOS", "指派SOS增援", sosId, "SUCCESS");
        realtimePublisher.publish("SOS_BACKUP_ASSIGNED", "sos", "SOS增援已指派", sosId + " ETA " + bo.backupEtaMinutes() + "分钟", sosId,
            realtimePublisher.payload("sosId", sosId, "backupEtaMinutes", bo.backupEtaMinutes(), "contactName", bo.contactName()));
        return R.ok(new SosActionVo(sosId, event.getPhase(), "增援信息已记录"));
    }

    @PostMapping("/sos/{sosId}/notify")
    public R<SosActionVo> notifySosContact(@PathVariable String sosId, @RequestBody SosContactBo bo) {
        if (sosEventMapper.selectById(sosId) == null) {
            return R.ok(new SosActionVo(sosId, "FAILED", "SOS事件不存在"));
        }
        if (bo.contactName() == null || bo.contactName().isBlank() || bo.contactPhone() == null || bo.contactPhone().isBlank()) {
            return R.ok(new SosActionVo(sosId, "FAILED", "请填写实际联系人和联系电话"));
        }
        saveSosDisposition(sosId, "MANUAL_CONTACT_CONFIRMED", "RECORDED", blankToDefault(bo.note(), "指挥员已人工确认通知"), bo.contactName(), bo.contactPhone(), null, null, null);
        logAudit("SOS", "记录人工联系人通知结果", sosId, "SUCCESS");
        realtimePublisher.publish("SOS_CONTACT_RECORDED", "sos", "SOS人工通知结果已记录", bo.contactName(), sosId,
            realtimePublisher.payload("sosId", sosId, "contactName", bo.contactName(), "contactPhone", bo.contactPhone()));
        return R.ok(new SosActionVo(sosId, "RECORDED", "人工通知结果已记录"));
    }

    @PostMapping("/sos/{sosId}/notes")
    public R<SosActionVo> addSosNote(@PathVariable String sosId, @RequestBody SosNoteBo bo) {
        if (sosEventMapper.selectById(sosId) == null) {
            return R.ok(new SosActionVo(sosId, "FAILED", "SOS事件不存在"));
        }
        saveSosDisposition(sosId, "NOTE", "RECORDED", bo.note(), null, null, null, null, null);
        logAudit("SOS", "记录SOS处置说明", sosId, "SUCCESS");
        return R.ok(new SosActionVo(sosId, "RECORDED", "处置说明已记录"));
    }

    @PostMapping("/sos/{sosId}/close")
    public R<SosActionVo> closeSos(@PathVariable String sosId) {
        PatrolSosEvent event = sosEventMapper.selectById(sosId);
        if (event != null) {
            event.setPhase("CLOSED");
            event.setMessage("平台端已处置关闭");
            event.setRecordingAudio(false);
            sosEventMapper.updateById(event);
            saveSosDisposition(sosId, "CLOSE", "CLOSED", "平台端已处置关闭", null, null, null, null, event.getBackupEtaMinutes());
        }
        logAudit("SOS", "关闭SOS求助", sosId, event == null ? "FAILED" : "SUCCESS");
        realtimePublisher.publish("SOS_CLOSED", "sos", event == null ? "SOS关闭失败" : "SOS求助已关闭", sosId, sosId,
            realtimePublisher.payload("sosId", sosId, "phase", event == null ? "FAILED" : "CLOSED"));
        return R.ok(new SosActionVo(sosId, event == null ? "FAILED" : "CLOSED", event == null ? "SOS事件不存在" : "SOS事件已关闭"));
    }

    @PostMapping("/messages/send")
    public R<MessageResultVo> sendMessage(@RequestBody MessageBo bo) {
        if (bo == null || bo.targetId() == null || bo.targetId().isBlank()) {
            throw new ServiceException("消息接收目标不能为空");
        }
        if (bo.content() == null || bo.content().isBlank()) {
            throw new ServiceException("消息内容不能为空");
        }
        String targetId = bo.targetId().trim();
        String targetType = blankToDefault(bo.targetType(), "SINGLE").trim().toUpperCase();
        if (!Set.of("SINGLE", "DEVICE", "ORG").contains(targetType)) {
            throw new ServiceException("不支持的消息目标类型：" + targetType);
        }
        Date now = new Date();
        String messageId = "MSG-" + UUID.randomUUID();
        List<MessageRecipient> recipients = resolveMessageRecipients(targetId, targetType);
        if (recipients.isEmpty()) {
            throw new ServiceException("未找到可接收消息的警员或已绑定设备");
        }
        PatrolMessage message = new PatrolMessage();
        message.setMessageId(messageId);
        message.setTenantId(currentTenantId());
        message.setTitle(blankToDefault(bo.title(), "指挥消息"));
        message.setContent(bo.content().trim());
        message.setTargetType(targetType);
        message.setTargetId(targetId);
        message.setTargetName(targetName(targetId, targetType));
        message.setChannel("APP");
        message.setStatus("SENT");
        message.setReadCount(0);
        message.setTotalCount(recipients.size());
        message.setSentAt(now);
        message.setDelFlag("0");
        messageMapper.insert(message);
        saveMessageReceipts(messageId, recipients, now);
        logAudit("MESSAGE", "发送指挥消息", messageId, "SUCCESS");
        realtimePublisher.publish("MESSAGE_SENT", "messages", "指挥消息已发送", message.getTitle(), messageId,
            realtimePublisher.payload("messageId", messageId, "targetType", message.getTargetType(), "targetId", message.getTargetId(), "title", message.getTitle(), "totalCount", message.getTotalCount()));
        return R.ok(new MessageResultVo(messageId, targetId, "SENT", formatDate(now)));
    }

    @GetMapping("/messages")
    public R<PageEnvelope<MessageVo>> messages(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(messageMapper, new LambdaQueryWrapper<PatrolMessage>()
            .orderByDesc(PatrolMessage::getSentAt), page, pageSize, this::toMessageVo));
    }

    @GetMapping("/messages/{messageId}/receipts")
    public R<List<MessageReceiptVo>> messageReceipts(@PathVariable String messageId) {
        return R.ok(messageReceiptMapper.selectList(new LambdaQueryWrapper<PatrolMessageReceipt>()
                .eq(PatrolMessageReceipt::getMessageId, messageId)
                .orderByAsc(PatrolMessageReceipt::getRecipientId))
            .stream()
            .map(this::toMessageReceiptVo)
            .toList());
    }

    @GetMapping("/control/persons")
    public R<PageEnvelope<ControlPersonVo>> controlPersons(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(controlPersonMapper, new LambdaQueryWrapper<PatrolControlPerson>()
            .orderByDesc(PatrolControlPerson::getCreateTime), page, pageSize, this::toControlPersonVo));
    }

    @PostMapping("/control/persons")
    public R<ControlPersonVo> createControlPerson(@RequestBody ControlPersonBo bo) {
        PatrolControlPerson person = new PatrolControlPerson();
        person.setControlId("CP-" + UUID.randomUUID());
        person.setTenantId(currentTenantId());
        person.setName(bo.name());
        person.setCategory(blankToDefault(bo.category(), "重点关注"));
        person.setIdCardNo(bo.idCardNo());
        person.setRiskLevel(blankToDefault(bo.riskLevel(), "MEDIUM"));
        person.setStatus("ENABLED");
        person.setSource("平台录入");
        person.setExpiresAt(parseDate(bo.expiresAt()));
        person.setRemark(bo.remark());
        person.setFaceImageUrl(bo.faceImageUrl());
        person.setFaceImageSha256(bo.faceImageSha256());
        if (bo.faceImageUrl() != null && !bo.faceImageUrl().isBlank()) {
            person.setFaceUpdatedAt(new Date());
        }
        person.setDelFlag("0");
        controlPersonMapper.insert(person);
        logAudit("CONTROL", "新增人员布控", person.getControlId(), "SUCCESS");
        return R.ok(toControlPersonVo(person));
    }

    @PatchMapping("/control/persons/{controlId}/face-image")
    public R<ControlPersonVo> updateControlPersonFaceImage(@PathVariable String controlId, @RequestBody ControlPersonFaceImageBo bo) {
        PatrolControlPerson person = controlPersonMapper.selectById(controlId);
        if (person == null) {
            throw new ServiceException("人员布控不存在");
        }
        person.setFaceImageUrl(bo.faceImageUrl());
        person.setFaceImageSha256(bo.faceImageSha256());
        person.setFaceUpdatedAt(new Date());
        controlPersonMapper.updateById(person);
        logAudit("CONTROL", "更新人员布控人脸底库", controlId, "SUCCESS");
        return R.ok(toControlPersonVo(person));
    }

    @PostMapping(value = "/control/persons/{controlId}/face-image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<ControlPersonFaceImageVo> uploadControlPersonFaceImage(@PathVariable String controlId, @RequestPart("file") MultipartFile file) {
        PatrolControlPerson person = controlPersonMapper.selectById(controlId);
        if (person == null) {
            throw new ServiceException("人员布控不存在");
        }
        String contentType = blankToDefault(file.getContentType(), "");
        String fileName = blankToDefault(file.getOriginalFilename(), controlId + ".jpg");
        String lowerName = fileName.toLowerCase();
        if (!contentType.startsWith("image/") && !lowerName.endsWith(".jpg") && !lowerName.endsWith(".jpeg") && !lowerName.endsWith(".png") && !lowerName.endsWith(".webp")) {
            throw new ServiceException("仅支持上传人脸图片");
        }
        String sha256 = sha256(file);
        SysOssVo oss = ossService.upload(file);
        String fileId = "FILE-" + oss.getOssId();
        String downloadUrl = "/files/" + fileId + "/download";

        PatrolMedia media = new PatrolMedia();
        media.setMediaId(IdUtil.getSnowflakeNextId());
        media.setTenantId(currentTenantId());
        media.setFileId(fileId);
        media.setFileName(fileName);
        media.setMediaType("IMAGE");
        media.setCapturedAt(formatDate(new Date()));
        media.setSizeText(sizeText(file.getSize()));
        media.setFileSizeBytes(file.getSize());
        media.setMimeType(blankToDefault(file.getContentType(), "image/jpeg"));
        media.setSha256Verified(true);
        media.setStorageSide("CONTROL_PERSON");
        media.setTransferStatus("DONE");
        media.setProgress(1F);
        media.setContentUri(downloadUrl);
        media.setOssId(oss.getOssId());
        media.setBucketName("ruoyi");
        media.setObjectKey(oss.getFileName());
        media.setSha256(sha256);
        media.setBizType("CONTROL_PERSON_FACE");
        media.setBizId(controlId);
        media.setEvidenceSource("CONTROL_PERSON_FACE");
        media.setDelFlag("0");
        mediaMapper.insert(media);

        person.setFaceImageUrl(downloadUrl);
        person.setFaceImageSha256(sha256);
        person.setFaceUpdatedAt(new Date());
        controlPersonMapper.updateById(person);
        logAudit("CONTROL", "上传人员布控人脸底库", controlId, "SUCCESS");
        return R.ok(new ControlPersonFaceImageVo(controlId, downloadUrl, sha256, formatDate(person.getFaceUpdatedAt())));
    }

    @PostMapping(value = "/control/persons/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<ImportResultVo> importControlPersons(@RequestPart("file") MultipartFile file) {
        ImportResultVo result = importPersons(file);
        logAudit("CONTROL", "导入人员布控", "control-person-import", result.failed() == 0 ? "SUCCESS" : "PARTIAL");
        return R.ok(result);
    }

    @PatchMapping("/control/persons/{controlId}/status")
    public R<ControlStatusVo> updateControlPersonStatus(@PathVariable String controlId, @RequestBody StatusBo bo) {
        PatrolControlPerson person = controlPersonMapper.selectById(controlId);
        if (person != null) {
            person.setStatus(bo.status());
            controlPersonMapper.updateById(person);
        }
        logAudit("CONTROL", "更新人员布控状态：" + bo.status(), controlId, person == null ? "FAILED" : "SUCCESS");
        return R.ok(new ControlStatusVo(controlId, bo.status(), person == null ? "人员布控不存在" : "人员布控状态已更新"));
    }

    @GetMapping("/control/vehicles")
    public R<PageEnvelope<ControlVehicleVo>> controlVehicles(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(controlVehicleMapper, new LambdaQueryWrapper<PatrolControlVehicle>()
            .orderByDesc(PatrolControlVehicle::getCreateTime), page, pageSize, this::toControlVehicleVo));
    }

    @PostMapping("/control/vehicles")
    public R<ControlVehicleVo> createControlVehicle(@RequestBody ControlVehicleBo bo) {
        PatrolControlVehicle vehicle = new PatrolControlVehicle();
        vehicle.setControlId("CV-" + UUID.randomUUID());
        vehicle.setTenantId(currentTenantId());
        vehicle.setPlateNo(bo.plateNo());
        vehicle.setVehicleDesc(blankToDefault(bo.vehicleDesc(), "未填写"));
        vehicle.setVehicleType(bo.vehicleType());
        vehicle.setRiskLevel(blankToDefault(bo.riskLevel(), "MEDIUM"));
        vehicle.setStatus("ENABLED");
        vehicle.setSource("平台录入");
        vehicle.setExpiresAt(parseDate(bo.expiresAt()));
        vehicle.setRemark(bo.remark());
        vehicle.setDelFlag("0");
        controlVehicleMapper.insert(vehicle);
        logAudit("CONTROL", "新增车辆布控", vehicle.getControlId(), "SUCCESS");
        return R.ok(toControlVehicleVo(vehicle));
    }

    @PostMapping(value = "/control/vehicles/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<ImportResultVo> importControlVehicles(@RequestPart("file") MultipartFile file) {
        ImportResultVo result = importVehicles(file);
        logAudit("CONTROL", "导入车辆布控", "control-vehicle-import", result.failed() == 0 ? "SUCCESS" : "PARTIAL");
        return R.ok(result);
    }

    @PatchMapping("/control/vehicles/{controlId}/status")
    public R<ControlStatusVo> updateControlVehicleStatus(@PathVariable String controlId, @RequestBody StatusBo bo) {
        PatrolControlVehicle vehicle = controlVehicleMapper.selectById(controlId);
        if (vehicle != null) {
            vehicle.setStatus(bo.status());
            controlVehicleMapper.updateById(vehicle);
        }
        logAudit("CONTROL", "更新车辆布控状态：" + bo.status(), controlId, vehicle == null ? "FAILED" : "SUCCESS");
        return R.ok(new ControlStatusVo(controlId, bo.status(), vehicle == null ? "车辆布控不存在" : "车辆布控状态已更新"));
    }

    @GetMapping("/statistics/overview")
    public R<StatisticsOverviewVo> statisticsOverview() {
        List<PatrolDevice> devices = deviceMapper.selectList();
        List<PatrolAlert> alerts = alertMapper.selectList();
        List<PatrolMedia> media = mediaMapper.selectList();
        List<PatrolSosEvent> sosEvents = sosEventMapper.selectList();
        long onlineDevices = devices.stream().filter(this::isDeviceOnline).count();
        long totalDevices = devices.size();
        long closedAlerts = alerts.stream().filter(item -> "CLOSED".equals(item.getStatus())).count();
        long pendingAlerts = alerts.size() - closedAlerts;
        String onlineRate = totalDevices == 0 ? "0%" : Math.round(onlineDevices * 100.0 / totalDevices) + "%";
        return R.ok(new StatisticsOverviewVo(
            List.of(
                new MetricVo("设备在线率", onlineRate, onlineDevices + "/" + totalDevices, "success"),
                new MetricVo("未处置预警", String.valueOf(pendingAlerts), "当前待处理", pendingAlerts > 0 ? "danger" : "success"),
                new MetricVo("SOS 激活", String.valueOf(sosEvents.stream().filter(item -> "ACTIVE".equals(item.getPhase())).count()), "当前 ACTIVE", "warning"),
                new MetricVo("媒体证据", String.valueOf(media.size()), "设备侧 + 云端", "info")
            ),
            List.of(
                new TrendPointVo("05-14", alerts.size(), sosEvents.size(), media.size(), commandMapper.selectCount(new LambdaQueryWrapper<>()).intValue())
            ),
            List.of(
                new RankingItemVo("设备指令", Math.toIntExact(commandMapper.selectCount(new LambdaQueryWrapper<PatrolDeviceCommand>())), "指令次数"),
                new RankingItemVo("未处置预警", Math.toIntExact(pendingAlerts), "当前积压"),
                new RankingItemVo("媒体待校验", Math.toIntExact(media.stream().filter(item -> !Boolean.TRUE.equals(item.getSha256Verified())).count()), "证据完整性")
            ),
            List.of(
                new RankingItemVo("已关闭", Math.toIntExact(closedAlerts), "告警"),
                new RankingItemVo("处理中", Math.toIntExact(alerts.stream().filter(item -> "HANDLING".equals(item.getStatus())).count()), "告警"),
                new RankingItemVo("待确认", Math.toIntExact(alerts.stream().filter(item -> "PENDING".equals(item.getStatus())).count()), "告警")
            )
        ));
    }

    @GetMapping("/system/audit-logs")
    public R<PageEnvelope<AuditLogVo>> auditLogs(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(pageOf(auditLogMapper, new LambdaQueryWrapper<PatrolAuditLog>()
            .orderByDesc(PatrolAuditLog::getOccurredAt), page, pageSize, this::toAuditLogVo));
    }

    @GetMapping("/system/health")
    public R<List<SystemHealthVo>> systemHealth() {
        List<SystemHealthVo> items = new ArrayList<>();
        items.add(apiHealth());
        items.add(databaseHealth());
        items.add(redisHealth());
        items.add(minioHealth());
        items.add(uploadQueueHealth());
        items.add(new SystemHealthVo("SSE 实时推送", "UP", "本机连接 " + sseEmitterManager.activeConnectionCount(), "Redis 主题广播已接入"));
        items.add(new SystemHealthVo("流媒体节点", "RESERVED", "ZLMediaKit/SRS/Janus", "SDK 暂未提供实时流能力，当前保留接口"));
        return R.ok(items);
    }

    @GetMapping("/integration/configs")
    public R<List<IntegrationConfigVo>> integrationConfigs() {
        return R.ok(List.of(
            new IntegrationConfigVo("FACE_MATCH", "云端人脸布控", "IMPLEMENTED", "PLCerebellum HTTP API", "同步平台人员库；真实模型比中后回传移动端，fallback 不生成正式告警"),
            new IntegrationConfigVo("PLATE_OCR", "云端车牌布控", "IMPLEMENTED", "PLCerebellum HTTP API", "同步平台车辆库；真实识别命中后由平台复核并回传移动端"),
            new IntegrationConfigVo("POLICE_110", "110 接处警平台", "RESERVED", "专网接口", "报警位置上图二期接入"),
            new IntegrationConfigVo("MAP_AMAP", "高德地图", "PLANNED", "JS SDK / Web API", "警力一张图底图与坐标服务")
        ));
    }

    @GetMapping("/system/capabilities")
    public R<Map<String, Object>> capabilities() {
        return R.ok(Map.of(
            "database", List.of("MySQL 8", "达梦 DM8", "人大金仓 KingbaseES V8"),
            "storage", "MinIO",
            "cache", "Redis/Redisson",
            "map", "高德地图",
            "videoLayouts", List.of(2, 4, 8, 12, 16),
            "algorithm", "PLCerebellum 云端人脸/车牌识别（真实模型结果）",
            "policeCallIntegration", "预留 110 接处警平台接口"
        ));
    }

    private <E, V> PageEnvelope<V> pageOf(BaseMapper<E> mapper, Wrapper<E> wrapper, int page, int pageSize, Function<E, V> converter) {
        int current = normalizePage(page);
        int size = normalizePageSize(pageSize);
        Page<E> result = mapper.selectPage(new Page<>(current, size), wrapper);
        List<V> items = result.getRecords().stream().map(converter).toList();
        return new PageEnvelope<>(items, current, size, result.getTotal(), current * (long) size < result.getTotal());
    }

    private int normalizePage(int page) {
        return Math.max(page, 1);
    }

    private int normalizePageSize(int pageSize) {
        return Math.min(Math.max(pageSize, 1), 100);
    }

    private SystemHealthVo apiHealth() {
        long uptimeSeconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        long maxMemoryMb = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        long usedMemoryMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        return new SystemHealthVo("业务 API", "UP", "JVM uptime " + uptimeSeconds + "s", "内存 " + usedMemoryMb + "/" + maxMemoryMb + " MB");
    }

    private SystemHealthVo databaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            boolean valid = connection.isValid(2);
            String product = metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion();
            String detail = valid ? "连接校验通过，URL=" + metaData.getURL() : "连接不可用";
            return new SystemHealthVo("数据库", valid ? "UP" : "DOWN", product, detail);
        } catch (SQLException e) {
            return new SystemHealthVo("数据库", "DOWN", "master", e.getMessage());
        }
    }

    private SystemHealthVo redisHealth() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        try {
            String pong = connection.ping();
            Long dbSize = connection.dbSize();
            String version = "unknown";
            if (connection.commands().info("server") != null) {
                version = connection.commands().info("server").getProperty("redis_version", "unknown");
            }
            boolean up = "PONG".equalsIgnoreCase(pong);
            return new SystemHealthVo("Redis", up ? "UP" : "DOWN", "Redis " + version, "PING=" + pong + "，key 数=" + value(dbSize, 0L));
        } catch (Exception e) {
            return new SystemHealthVo("Redis", "DOWN", "Redisson", e.getMessage());
        } finally {
            RedisConnectionUtils.releaseConnection(connection, redisConnectionFactory);
        }
    }

    private SystemHealthVo minioHealth() {
        SysOssConfig config = ossConfigMapper.selectList(new LambdaQueryWrapper<SysOssConfig>()
                .eq(SysOssConfig::getStatus, "0"))
            .stream()
            .findFirst()
            .orElse(null);
        if (config == null) {
            return new SystemHealthVo("MinIO/对象存储", "WARN", "未找到默认 OSS 配置", "请在 sys_oss_config 启用默认配置");
        }
        String endpoint = config.getEndpoint();
        String scheme = "Y".equalsIgnoreCase(config.getIsHttps()) ? "https://" : "http://";
        String healthUrl = scheme + endpoint + "/minio/health/live";
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(healthUrl))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();
            int statusCode = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.discarding())
                .statusCode();
            String status = statusCode >= 200 && statusCode < 500 ? "UP" : "DOWN";
            return new SystemHealthVo("MinIO/对象存储", status, config.getBucketName() + "@" + endpoint, "health=" + statusCode + "，默认配置=" + config.getConfigKey());
        } catch (Exception e) {
            return new SystemHealthVo("MinIO/对象存储", "DOWN", config.getBucketName() + "@" + endpoint, e.getMessage());
        }
    }

    private SystemHealthVo uploadQueueHealth() {
        List<PatrolMediaUploadTask> tasks = mediaUploadTaskMapper.selectList(new LambdaQueryWrapper<>());
        long active = tasks.stream().filter(item -> !"COMPLETED".equals(item.getStatus()) && !"EXPIRED".equals(item.getStatus())).count();
        long failed = tasks.stream().filter(item -> "FAILED".equals(item.getStatus())).count();
        String status = failed > 0 ? "WARN" : "UP";
        return new SystemHealthVo("媒体上传队列", status, active + " 个进行中", "总任务=" + tasks.size() + "，失败=" + failed);
    }

    private List<WorkItemVo> workItems(List<PatrolAlert> alerts, List<PatrolSosEvent> sosEvents, List<PatrolDevice> devices) {
        List<WorkItemVo> items = new ArrayList<>();
        alerts.stream()
            .filter(item -> !"CLOSED".equals(item.getStatus()))
            .limit(3)
            .forEach(item -> items.add(new WorkItemVo(item.getAlertId(), item.getTitle(), officerNameByDevice(item.getSource()), item.getSource(), item.getStatus(), item.getLevel(), blankToDefault(item.getOccurredAt(), "刚刚"))));
        sosEvents.stream()
            .filter(item -> "ACTIVE".equals(item.getPhase()))
            .limit(2)
            .forEach(item -> items.add(new WorkItemVo(item.getSosId(), "一键 SOS 求助", "移动端警员", blankToDefault(item.getAddress(), "未知位置"), item.getPhase(), "CRITICAL", formatDate(item.getCreateTime()))));
        devices.stream()
            .filter(this::isDeviceOnline)
            .filter(item -> value(item.getBatteryPercent()) > 0 && value(item.getBatteryPercent()) < 20)
            .limit(2)
            .forEach(item -> items.add(new WorkItemVo("device-" + item.getDeviceId(), "设备低电量", officerName(item), item.getDeviceId() + " 电量 " + item.getBatteryPercent() + "%", "待确认", "WARNING", formatDate(item.getLastHeartbeatTime()))));
        return items.stream().limit(6).toList();
    }

    private DeviceVo toDeviceVo(PatrolDevice device) {
        return new DeviceVo(
            device.getDeviceId(),
            device.getDeviceName(),
            officerName(device),
            badgeNo(device),
            deptName(device),
            isDeviceOnline(device) ? "ONLINE" : "OFFLINE",
            value(device.getBatteryPercent()),
            value(device.getSignalBars()),
            blankToDefault(device.getFirmwareVersion(), "-"),
            String.format("%.1fGB/%.1fGB", value(device.getStorageUsedGb()), value(device.getStorageTotalGb())),
            workState(device),
            formatDate(device.getLastHeartbeatTime())
        );
    }

    private DeviceConfigVo toDeviceConfigVo(PatrolDevice device) {
        DeviceCapabilitiesDto capabilities = patrolAppService.deviceCapabilities(device.getDeviceId());
        DeviceWifiStateDto wifi = patrolAppService.deviceWifi(device.getDeviceId());
        PatrolDeviceConfig config = loadDeviceConfig(device.getDeviceId());
        return new DeviceConfigVo(
            device.getDeviceId(),
            device.getDeviceName(),
            officerName(device),
            badgeNo(device),
            deptName(device),
            capabilities,
            wifi,
            toDeviceSettingsDto(config),
            false,
            config == null ? "-" : formatDate(config.getLastMediaSyncAt())
        );
    }

    private DeviceAdvancedSettingsDto toDeviceSettingsDto(PatrolDeviceConfig config) {
        if (config == null) {
            return new DeviceAdvancedSettingsDto(240, 0, 16, 24 * 60 * 60, true, true, 2);
        }
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

    private PatrolDeviceConfig loadDeviceConfig(String deviceId) {
        return deviceConfigMapper.selectOne(new LambdaQueryWrapper<PatrolDeviceConfig>()
            .eq(PatrolDeviceConfig::getDeviceId, deviceId)
            .last("limit 1"));
    }

    private PatrolDevice deviceOrFallback(String deviceId) {
        PatrolDevice device = deviceMapper.selectById(deviceId);
        return device == null ? fallbackDevice(deviceId) : device;
    }

    private PatrolDevice fallbackDevice(String deviceId) {
        PatrolDevice device = new PatrolDevice();
        device.setDeviceId(deviceId);
        device.setDeviceName(deviceId);
        return device;
    }

    private void applyDeviceCommand(PatrolDevice device, String command) {
        if ("START_RECORD".equals(command)) {
            device.setRecordingStatus("RECORDING");
        } else if ("STOP_RECORD".equals(command) || "STOP_STREAM".equals(command)) {
            device.setRecordingStatus("IDLE");
            device.setTalking(false);
        } else if ("START_TALK".equals(command)) {
            device.setTalking(true);
        } else if ("STOP_TALK".equals(command)) {
            device.setTalking(false);
        }
        device.setOnline(true);
        device.setCloudConnected(true);
        device.setLastHeartbeatTime(new Date());
    }

    private AlertVo toAlertVo(PatrolAlert alert) {
        return new AlertVo(
            alert.getAlertId(),
            alertType(alert),
            alert.getTitle(),
            blankToDefault(alert.getDescription(), alert.getTitle()),
            blankToDefault(alert.getSource(), "-"),
            officerNameByDevice(alert.getSource()),
            blankToDefault(alert.getLocationText(), "-"),
            alert.getStatus(),
            alert.getLevel(),
            blankToDefault(alert.getConfidence(), "-"),
            blankToDefault(alert.getOccurredAt(), formatDate(alert.getCreateTime()))
        );
    }

    private MediaVo toMediaVo(PatrolMedia media) {
        return new MediaVo(
            media.getFileId(),
            media.getFileName(),
            media.getMediaType(),
            blankToDefault(deviceIdByMedia(media), "-"),
            blankToDefault(media.getOfficerName(), officerNameByDevice(deviceIdByMedia(media))),
            blankToDefault(media.getBizType(), media.getStorageSide()) + "/" + blankToDefault(media.getBizId(), "-"),
            blankToDefault(media.getSizeText(), "-"),
            Boolean.TRUE.equals(media.getSha256Verified()) ? "VERIFIED" : blankToDefault(media.getTransferStatus(), "PENDING"),
            storagePath(media),
            blankToDefault(media.getCapturedAt(), formatDate(media.getCreateTime())),
            media.getSha256(),
            media.getWatermarkToken(),
            blankToDefault(media.getMimeType(), mediaMimeType(media)),
            media.getFileSizeBytes(),
            blankToDefault(media.getContentUri(), "/files/" + media.getFileId() + "/download")
        );
    }

    private DailyReportVo toDailyReportVo(PatrolDailyReport report) {
        if (report == null) {
            return null;
        }
        return new DailyReportVo(
            report.getReportId(),
            blankToDefault(report.getMissionId(), "-"),
            blankToDefault(report.getReportType(), "daily"),
            blankToDefault(report.getDeviceId(), "-"),
            blankToDefault(report.getOperatorId(), "-"),
            blankToDefault(report.getOfficerName(), "-"),
            blankToDefault(report.getModel(), "-"),
            blankToDefault(report.getBackend(), "-"),
            formatDate(report.getGeneratedAt()),
            blankToDefault(report.getContent(), ""),
            blankToDefault(report.getDocumentUri(), ""),
            blankToDefault(report.getDocumentName(), ""),
            blankToDefault(report.getDocumentFormat(), "docx"),
            blankToDefault(report.getMediaSelectionJson(), "{}"),
            blankToDefault(report.getStructuredContextJson(), "{}"),
            Boolean.TRUE.equals(report.getRequiresHumanConfirmation()),
            blankToDefault(report.getStatus(), "PENDING_REVIEW"),
            blankToDefault(report.getSubmitSource(), "CEREBELLUM")
        );
    }

    private AppVersionVo toAppVersionVo(PatrolAppVersion version) {
        return new AppVersionVo(
            version.getVersionId(),
            version.getVersionCode(),
            version.getVersionName(),
            Boolean.TRUE.equals(version.getForceUpdate()),
            blankToDefault(version.getChangelog(), ""),
            blankToDefault(version.getDownloadUrl(), ""),
            blankToDefault(version.getSha256(), ""),
            blankToDefault(version.getFileId(), ""),
            blankToDefault(version.getStatus(), "DRAFT"),
            formatDate(version.getPublishedAt())
        );
    }

    private FirmwareVersionVo toFirmwareVersionVo(PatrolFirmwareVersion firmware) {
        return new FirmwareVersionVo(
            firmware.getFirmwareId(),
            blankToDefault(firmware.getDeviceType(), ""),
            blankToDefault(firmware.getVendor(), ""),
            blankToDefault(firmware.getChipset(), ""),
            blankToDefault(firmware.getDeviceModel(), ""),
            blankToDefault(firmware.getHardwareVersion(), ""),
            blankToDefault(firmware.getFirmwareType(), ""),
            firmware.getVersionCode(),
            blankToDefault(firmware.getVersionName(), ""),
            blankToDefault(firmware.getMinCurrentVersion(), ""),
            blankToDefault(firmware.getMaxCurrentVersion(), ""),
            Boolean.TRUE.equals(firmware.getForceUpdate()),
            blankToDefault(firmware.getChangelog(), ""),
            blankToDefault(firmware.getDownloadUrl(), ""),
            blankToDefault(firmware.getSha256(), ""),
            blankToDefault(firmware.getFileId(), ""),
            value(firmware.getFileSizeBytes(), 0L),
            blankToDefault(firmware.getPackageFormat(), ""),
            blankToDefault(firmware.getUpgradeMode(), "APP_BLE"),
            blankToDefault(firmware.getGrayScope(), "ALL"),
            blankToDefault(firmware.getGrayTargets(), ""),
            blankToDefault(firmware.getStatus(), "DRAFT"),
            formatDate(firmware.getPublishedAt()),
            blankToDefault(firmware.getRemark(), "")
        );
    }

    private FirmwareUpgradeTaskVo toFirmwareUpgradeTaskVo(PatrolFirmwareUpgradeTask task) {
        return new FirmwareUpgradeTaskVo(
            task.getTaskId(),
            task.getDeviceId(),
            task.getFirmwareId(),
            blankToDefault(task.getOperatorId(), ""),
            blankToDefault(task.getFromVersion(), ""),
            blankToDefault(task.getToVersion(), ""),
            blankToDefault(task.getStatus(), "PENDING"),
            value(task.getProgress()),
            blankToDefault(task.getErrorCode(), ""),
            blankToDefault(task.getErrorMessage(), ""),
            formatDate(task.getStartedAt()),
            formatDate(task.getFinishedAt())
        );
    }

    private SosVo toSosVo(PatrolSosEvent event) {
        PatrolDevice device = event.getDeviceId() == null || event.getDeviceId().isBlank() ? null : deviceMapper.selectById(event.getDeviceId());
        return new SosVo(
            event.getSosId(),
            blankToDefault(event.getOfficerName(), device == null ? "-" : officerName(device)),
            blankToDefault(event.getBadgeNo(), device == null ? "-" : badgeNo(device)),
            blankToDefault(event.getDeptName(), device == null ? "-" : deptName(device)),
            blankToDefault(event.getDeviceId(), ""),
            blankToDefault(event.getAddress(), "-"),
            event.getLatitude(),
            event.getLongitude(),
            event.getAccuracyMeters(),
            event.getPhase(),
            blankToDefault(event.getMessage(), "等待处置"),
            Boolean.TRUE.equals(event.getRecordingAudio()),
            event.getBackupEtaMinutes(),
            formatDate(event.getCreateTime())
        );
    }

    private SosTimelineVo toSosTimelineVo(PatrolSosDisposition disposition) {
        return new SosTimelineVo(
            disposition.getDispositionId(),
            disposition.getSosId(),
            disposition.getActionType(),
            disposition.getActionResult(),
            blankToDefault(disposition.getOperatorName(), "-"),
            blankToDefault(disposition.getNote(), "-"),
            disposition.getContactName(),
            disposition.getContactPhone(),
            disposition.getAttachmentFileId(),
            disposition.getAttachmentFileName(),
            disposition.getBackupEtaMinutes(),
            formatDate(disposition.getOccurredAt())
        );
    }

    private TrackPointVo toTrackPointVo(PatrolLocationTrack track) {
        return new TrackPointVo(
            track.getBadgeNo(),
            BigDecimal.valueOf(track.getLatitude()),
            BigDecimal.valueOf(track.getLongitude()),
            blankToDefault(track.getAddress(), "-"),
            formatDate(track.getReportedAt())
        );
    }

    private PatrolArea currentArea() {
        return areaMapper.selectList(new LambdaQueryWrapper<PatrolArea>()
                .orderByDesc(PatrolArea::getUpdateTime)
                .orderByDesc(PatrolArea::getCreateTime))
            .stream()
            .findFirst()
            .orElse(null);
    }

    private PatrolAreaVo toPatrolAreaVo(PatrolArea area) {
        if (area == null) {
            return fallbackPatrolArea();
        }
        return new PatrolAreaVo(
            area.getAreaId(),
            area.getAreaName(),
            area.getTeamId(),
            area.getTeamName(),
            blankToDefault(area.getOwnerType(), "TEAM"),
            area.getUserId() == null ? "" : String.valueOf(area.getUserId()),
            blankToDefault(area.getBadgeNo(), ""),
            parseGeoPoints(area.getBoundaryJson()),
            parseGeoPoints(area.getRouteJson()),
            formatDate(area.getUpdateTime() == null ? area.getCreateTime() : area.getUpdateTime())
        );
    }

    private PatrolAreaVo fallbackPatrolArea() {
        return new PatrolAreaVo("", "未配置巡区", "", "", "UNASSIGNED", "", "", List.of(), List.of(), "-");
    }

    private List<PatrolGeoPointVo> parseGeoPoints(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        List<PatrolGeoPointVo> points = JsonUtils.parseArray(json, PatrolGeoPointVo.class);
        return points == null ? List.of() : points;
    }

    private Long parseLongOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Long.valueOf(value);
    }

    private String alertType(PatrolAlert alert) {
        String text = blankToDefault(alert.getTitle(), "") + blankToDefault(alert.getDescription(), "");
        if (text.contains("车")) {
            return "VEHICLE";
        }
        if (text.contains("声") || text.contains("音")) {
            return "AUDIO";
        }
        return "PERSON";
    }

    private String workState(PatrolDevice device) {
        if (!isDeviceOnline(device)) {
            return "OFFLINE";
        }
        if (value(device.getBatteryPercent()) < 20) {
            return "LOW_BATTERY";
        }
        if (Boolean.TRUE.equals(device.getTalking())) {
            return "TALKING";
        }
        return blankToDefault(device.getRecordingStatus(), "IDLE");
    }

    private boolean isDeviceOnline(PatrolDevice device) {
        if (device == null || !Boolean.TRUE.equals(device.getOnline()) || device.getLastHeartbeatTime() == null) {
            return false;
        }
        return System.currentTimeMillis() - device.getLastHeartbeatTime().getTime() <= DEVICE_HEARTBEAT_TIMEOUT_MILLIS;
    }

    private String officerNameByDevice(String deviceId) {
        PatrolDevice device = deviceId == null ? null : deviceMapper.selectById(deviceId);
        return device == null ? "系统算法" : officerName(device);
    }

    private String officerName(PatrolDevice device) {
        PatrolDeviceBinding binding = activeBinding(device.getDeviceId());
        return binding == null ? "未绑定警员" : blankToDefault(binding.getNickName(), binding.getUserName());
    }

    private String badgeNo(PatrolDevice device) {
        PatrolDeviceBinding binding = activeBinding(device.getDeviceId());
        return binding == null ? "-" : blankToDefault(binding.getBadgeNo(), binding.getUserName());
    }

    private String deptName(PatrolDevice device) {
        PatrolDeviceBinding binding = activeBinding(device.getDeviceId());
        return binding == null ? "未分配" : blankToDefault(binding.getDeptName(), "未分配");
    }

    private String deviceIdByMedia(PatrolMedia media) {
        if (media.getDeviceId() != null && !media.getDeviceId().isBlank()) {
            return media.getDeviceId();
        }
        return null;
    }

    private String storagePath(PatrolMedia media) {
        String bucket = blankToDefault(media.getBucketName(), "ruoyi");
        String objectKey = blankToDefault(media.getObjectKey(), media.getContentUri());
        return objectKey == null ? bucket : bucket + "/" + objectKey;
    }

    private String mediaMimeType(PatrolMedia media) {
        if ("PHOTO".equals(media.getMediaType())) {
            return "image/jpeg";
        }
        if ("AUDIO".equals(media.getMediaType())) {
            return "audio/mpeg";
        }
        return "video/mp4";
    }

    private CommandLogVo toCommandLogVo(PatrolDeviceCommand command) {
        return new CommandLogVo(
            command.getCommandId(),
            command.getDeviceId(),
            command.getCommand(),
            blankToDefault(command.getOperatorId(), "-"),
            blankToDefault(command.getStatus(), "-"),
            blankToDefault(command.getResultMessage(), "-"),
            formatDate(command.getSentAt()),
            formatDate(command.getAckAt())
        );
    }

    private DeviceEventVo toDeviceEventVo(PatrolDeviceEvent event) {
        return new DeviceEventVo(
            event.getEventId(),
            event.getDeviceId(),
            event.getEventType(),
            event.getEventLevel(),
            event.getEventTitle(),
            blankToDefault(event.getEventDetail(), "-"),
            formatDate(event.getOccurredAt())
        );
    }

    private AlertAttachmentVo toAlertAttachmentVo(PatrolAlertAttachment attachment) {
        return new AlertAttachmentVo(
            attachment.getAttachmentId(),
            attachment.getAlertId(),
            attachment.getClientFileId(),
            attachment.getFileName(),
            attachment.getMimeType(),
            attachment.getSizeBytes(),
            attachment.getSource(),
            attachment.getLocalUri(),
            attachment.getUploadIntent()
        );
    }

    private AlertDispositionVo toAlertDispositionVo(PatrolAlertDisposition disposition) {
        return new AlertDispositionVo(
            disposition.getDispositionId(),
            disposition.getAlertId(),
            disposition.getActionType(),
            blankToDefault(disposition.getActionResult(), "-"),
            blankToDefault(disposition.getOperatorName(), "-"),
            blankToDefault(disposition.getNote(), "-"),
            value(disposition.getAttachmentsCount()),
            formatDate(disposition.getOccurredAt())
        );
    }

    private ControlPersonVo toControlPersonVo(PatrolControlPerson person) {
        return new ControlPersonVo(
            person.getControlId(),
            person.getName(),
            blankToDefault(person.getCategory(), "-"),
            blankToDefault(person.getRiskLevel(), "-"),
            blankToDefault(person.getStatus(), "-"),
            blankToDefault(person.getSource(), "-"),
            formatDate(person.getExpiresAt()),
            person.getFaceImageUrl(),
            person.getFaceImageSha256(),
            formatDate(person.getFaceUpdatedAt()),
            person.getFaceImageUrl() != null && !person.getFaceImageUrl().isBlank()
        );
    }

    private ControlVehicleVo toControlVehicleVo(PatrolControlVehicle vehicle) {
        return new ControlVehicleVo(
            vehicle.getControlId(),
            vehicle.getPlateNo(),
            blankToDefault(vehicle.getVehicleDesc(), "-"),
            blankToDefault(vehicle.getRiskLevel(), "-"),
            blankToDefault(vehicle.getStatus(), "-"),
            blankToDefault(vehicle.getSource(), "-"),
            formatDate(vehicle.getExpiresAt())
        );
    }

    private ImportResultVo importPersons(MultipartFile file) {
        List<ImportErrorVo> errors = new ArrayList<>();
        Set<String> keys = new HashSet<>();
        int total = 0;
        int success = 0;
        for (Row row : excelRows(file)) {
            if (isHeader(row, "姓名") || isEmptyRow(row)) {
                continue;
            }
            total++;
            int rowNo = row.getRowNum() + 1;
            String name = cell(row, 0);
            String category = cell(row, 1);
            String idCardNo = cell(row, 2);
            String riskLevel = cell(row, 3);
            String expiresAt = cell(row, 4);
            String remark = cell(row, 5);
            if (name.isBlank()) {
                errors.add(new ImportErrorVo(rowNo, "姓名不能为空"));
                continue;
            }
            String key = idCardNo.isBlank() ? "NAME:" + name : "ID:" + idCardNo;
            if (!keys.add(key)) {
                errors.add(new ImportErrorVo(rowNo, "文件内存在重复人员：" + name));
                continue;
            }
            PatrolControlPerson person = findControlPerson(name, idCardNo);
            boolean create = person == null;
            if (create) {
                person = new PatrolControlPerson();
                person.setControlId("CP-" + UUID.randomUUID());
                person.setTenantId(currentTenantId());
                person.setDelFlag("0");
            }
            person.setName(name);
            person.setCategory(blankToDefault(category, "重点关注"));
            person.setIdCardNo(idCardNo);
            person.setRiskLevel(blankToDefault(riskLevel, "MEDIUM"));
            person.setStatus("ENABLED");
            person.setSource("Excel导入");
            person.setExpiresAt(parseDate(expiresAt));
            person.setRemark(remark);
            if (create) {
                controlPersonMapper.insert(person);
            } else {
                controlPersonMapper.updateById(person);
            }
            success++;
        }
        return importResult(total, success, errors, "人员布控导入");
    }

    private ImportResultVo importVehicles(MultipartFile file) {
        List<ImportErrorVo> errors = new ArrayList<>();
        Set<String> plateNos = new HashSet<>();
        int total = 0;
        int success = 0;
        for (Row row : excelRows(file)) {
            if (isHeader(row, "车牌") || isEmptyRow(row)) {
                continue;
            }
            total++;
            int rowNo = row.getRowNum() + 1;
            String plateNo = cell(row, 0).toUpperCase();
            String vehicleDesc = cell(row, 1);
            String vehicleType = cell(row, 2);
            String riskLevel = cell(row, 3);
            String expiresAt = cell(row, 4);
            String remark = cell(row, 5);
            if (plateNo.isBlank()) {
                errors.add(new ImportErrorVo(rowNo, "车牌号不能为空"));
                continue;
            }
            if (!plateNos.add(plateNo)) {
                errors.add(new ImportErrorVo(rowNo, "文件内存在重复车牌：" + plateNo));
                continue;
            }
            PatrolControlVehicle vehicle = controlVehicleMapper.selectOne(new LambdaQueryWrapper<PatrolControlVehicle>()
                .eq(PatrolControlVehicle::getPlateNo, plateNo)
                .last("limit 1"));
            boolean create = vehicle == null;
            if (create) {
                vehicle = new PatrolControlVehicle();
                vehicle.setControlId("CV-" + UUID.randomUUID());
                vehicle.setTenantId(currentTenantId());
                vehicle.setDelFlag("0");
            }
            vehicle.setPlateNo(plateNo);
            vehicle.setVehicleDesc(blankToDefault(vehicleDesc, "未填写"));
            vehicle.setVehicleType(vehicleType);
            vehicle.setRiskLevel(blankToDefault(riskLevel, "MEDIUM"));
            vehicle.setStatus("ENABLED");
            vehicle.setSource("Excel导入");
            vehicle.setExpiresAt(parseDate(expiresAt));
            vehicle.setRemark(remark);
            if (create) {
                controlVehicleMapper.insert(vehicle);
            } else {
                controlVehicleMapper.updateById(vehicle);
            }
            success++;
        }
        return importResult(total, success, errors, "车辆布控导入");
    }

    private PatrolControlPerson findControlPerson(String name, String idCardNo) {
        LambdaQueryWrapper<PatrolControlPerson> wrapper = new LambdaQueryWrapper<>();
        if (idCardNo != null && !idCardNo.isBlank()) {
            wrapper.eq(PatrolControlPerson::getIdCardNo, idCardNo);
        } else {
            wrapper.eq(PatrolControlPerson::getName, name);
        }
        return controlPersonMapper.selectOne(wrapper.last("limit 1"));
    }

    private ImportResultVo importResult(int total, int success, List<ImportErrorVo> errors, String title) {
        int failed = errors.size();
        String message = title + "完成：成功 " + success + " 行，失败 " + failed + " 行";
        return new ImportResultVo("IMPORT-" + System.currentTimeMillis(), total, success, failed, message, errors);
    }

    private List<Row> excelRows(MultipartFile file) {
        String fileName = blankToDefault(file.getOriginalFilename(), "");
        if (!fileName.toLowerCase().endsWith(".xlsx") && !fileName.toLowerCase().endsWith(".xls")) {
            throw new ServiceException("仅支持上传 xlsx/xls 文件");
        }
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Row> rows = new ArrayList<>();
            for (Row row : sheet) {
                rows.add(row);
            }
            return rows;
        } catch (Exception e) {
            throw new ServiceException("解析Excel失败：" + e.getMessage());
        }
    }

    private boolean isHeader(Row row, String expectedFirstCell) {
        String firstCell = cell(row, 0);
        return firstCell.contains(expectedFirstCell) || firstCell.equalsIgnoreCase("name") || firstCell.equalsIgnoreCase("plateNo");
    }

    private boolean isEmptyRow(Row row) {
        for (int index = 0; index < 6; index++) {
            if (!cell(row, index).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String cell(Row row, int index) {
        if (row == null) {
            return "";
        }
        Cell cell = row.getCell(index);
        return cell == null ? "" : new DataFormatter().formatCellValue(cell).trim();
    }

    private MessageVo toMessageVo(PatrolMessage message) {
        List<PatrolMessageReceipt> receipts = messageReceiptMapper.selectList(new LambdaQueryWrapper<PatrolMessageReceipt>()
            .eq(PatrolMessageReceipt::getMessageId, message.getMessageId()));
        int deliveredCount = Math.toIntExact(receipts.stream().filter(item -> "DELIVERED".equals(item.getDeliveryStatus()) || "READ".equals(item.getDeliveryStatus())).count());
        int pendingCount = receipts.isEmpty() ? Math.max(value(message.getTotalCount()) - deliveredCount, 0) : Math.max(receipts.size() - deliveredCount, 0);
        return new MessageVo(
            message.getMessageId(),
            blankToDefault(message.getTitle(), "指挥消息"),
            message.getContent(),
            message.getTargetType(),
            blankToDefault(message.getTargetName(), targetName(message.getTargetId(), message.getTargetType())),
            blankToDefault(message.getChannel(), "APP"),
            blankToDefault(message.getStatus(), "SENT"),
            deliveredCount,
            pendingCount,
            value(message.getReadCount()),
            value(message.getTotalCount()),
            formatDate(message.getSentAt())
        );
    }

    private MessageReceiptVo toMessageReceiptVo(PatrolMessageReceipt receipt) {
        return new MessageReceiptVo(
            receipt.getReceiptId(),
            receipt.getMessageId(),
            receipt.getRecipientId(),
            blankToDefault(receipt.getRecipientName(), "-"),
            blankToDefault(receipt.getDeviceId(), "-"),
            blankToDefault(receipt.getDeliveryStatus(), "PENDING"),
            formatDate(receipt.getDeliveredAt()),
            formatDate(receipt.getReadAt()),
            formatDate(receipt.getLastPullAt())
        );
    }

    private AuditLogVo toAuditLogVo(PatrolAuditLog log) {
        return new AuditLogVo(
            log.getLogId(),
            log.getLogType(),
            blankToDefault(log.getOperatorName(), "-"),
            log.getAction(),
            blankToDefault(log.getResource(), "-"),
            blankToDefault(log.getResult(), "-"),
            blankToDefault(log.getIpAddress(), "-"),
            blankToDefault(log.getTraceId(), "-"),
            formatDate(log.getOccurredAt())
        );
    }

    private void logAudit(String logType, String action, String resource, String result) {
        PatrolAuditLog log = new PatrolAuditLog();
        log.setLogId("AUD-" + UUID.randomUUID());
        log.setTenantId(currentTenantId());
        log.setLogType(logType);
        log.setOperatorName(currentOperator());
        log.setAction(action);
        log.setResource(resource);
        log.setResult(result);
        log.setIpAddress("127.0.0.1");
        log.setTraceId(UUID.randomUUID().toString());
        log.setOccurredAt(new Date());
        log.setDelFlag("0");
        auditLogMapper.insert(log);
    }

    private void saveDeviceEvent(String deviceId, String eventType, String eventLevel, String eventTitle, String eventDetail) {
        PatrolDeviceEvent event = new PatrolDeviceEvent();
        event.setEventId("EVT-" + UUID.randomUUID());
        event.setTenantId(currentTenantId());
        event.setDeviceId(deviceId);
        event.setEventType(eventType);
        event.setEventLevel(eventLevel);
        event.setEventTitle(eventTitle);
        event.setEventDetail(eventDetail);
        event.setOccurredAt(new Date());
        event.setDelFlag("0");
        deviceEventMapper.insert(event);
    }

    private void saveAlertAttachments(String alertId, List<AlertAttachmentBo> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        attachments.forEach(item -> {
            PatrolAlertAttachment attachment = new PatrolAlertAttachment();
            attachment.setAttachmentId("ATT-" + UUID.randomUUID());
            attachment.setTenantId(currentTenantId());
            attachment.setAlertId(alertId);
            attachment.setClientFileId(item.clientFileId());
            attachment.setFileName(item.fileName());
            attachment.setMimeType(item.mimeType());
            attachment.setSizeBytes(item.sizeBytes());
            attachment.setSource(item.source());
            attachment.setLocalUri(item.localUri());
            attachment.setUploadIntent(item.uploadIntent());
            attachment.setDelFlag("0");
            alertAttachmentMapper.insert(attachment);
        });
    }

    private void saveAlertDisposition(String alertId, String actionType, String actionResult, String note, int attachmentsCount) {
        PatrolAlertDisposition disposition = new PatrolAlertDisposition();
        disposition.setDispositionId("AD-" + UUID.randomUUID());
        disposition.setTenantId(currentTenantId());
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

    private void saveSosDisposition(String sosId, String actionType, String actionResult, String note, String contactName, String contactPhone, String attachmentFileId, String attachmentFileName, Integer backupEtaMinutes) {
        PatrolSosDisposition disposition = new PatrolSosDisposition();
        disposition.setDispositionId("SD-" + UUID.randomUUID());
        disposition.setTenantId(currentTenantId());
        disposition.setSosId(sosId);
        disposition.setActionType(actionType);
        disposition.setActionResult(actionResult);
        disposition.setOperatorId(currentOperator());
        disposition.setOperatorName(currentOperator());
        disposition.setNote(note);
        disposition.setContactName(contactName);
        disposition.setContactPhone(contactPhone);
        disposition.setAttachmentFileId(attachmentFileId);
        disposition.setAttachmentFileName(attachmentFileName);
        disposition.setBackupEtaMinutes(backupEtaMinutes);
        disposition.setOccurredAt(new Date());
        disposition.setDelFlag("0");
        sosDispositionMapper.insert(disposition);
    }

    private List<MessageRecipient> resolveMessageRecipients(String targetId, String targetType) {
        List<MessageRecipient> recipients = new ArrayList<>();
        if ("ORG".equals(targetType)) {
            Long deptId = parseLongOrNull(targetId);
            if (deptId == null) {
                throw new ServiceException("组织目标必须使用有效的组织ID");
            }
            deviceBindingMapper.selectList(new LambdaQueryWrapper<PatrolDeviceBinding>()
                    .eq(PatrolDeviceBinding::getBindStatus, "BOUND")
                    .eq(PatrolDeviceBinding::getDeptId, deptId))
                .forEach(binding -> recipients.add(toMessageRecipient(binding, targetId)));
        } else if ("DEVICE".equals(targetType)) {
            PatrolDeviceBinding binding = activeBinding(targetId);
            if (binding == null) {
                throw new ServiceException("目标设备未绑定在线警员：" + targetId);
            }
            recipients.add(toMessageRecipient(binding, targetId));
        } else {
            PatrolDeviceBinding binding = activeBindingByBadgeNo(targetId);
            if (binding == null) {
                recipients.add(new MessageRecipient(targetId, targetName(targetId, targetType), null));
            } else {
                recipients.add(toMessageRecipient(binding, targetId));
            }
        }
        return recipients.stream()
            .filter(item -> item.recipientId() != null && !item.recipientId().isBlank())
            .collect(ArrayList::new, (list, item) -> {
                boolean exists = list.stream().anyMatch(current -> current.recipientId().equals(item.recipientId()));
                if (!exists) {
                    list.add(item);
                }
            }, ArrayList::addAll);
    }

    private MessageRecipient toMessageRecipient(PatrolDeviceBinding binding, String fallbackId) {
        return new MessageRecipient(
            blankToDefault(binding.getBadgeNo(), blankToDefault(binding.getUserName(), fallbackId)),
            blankToDefault(binding.getNickName(), binding.getUserName()),
            binding.getDeviceId()
        );
    }

    private void saveMessageReceipts(String messageId, List<MessageRecipient> recipients, Date now) {
        for (MessageRecipient recipient : recipients) {
            PatrolMessageReceipt receipt = new PatrolMessageReceipt();
            receipt.setReceiptId("MR-" + UUID.randomUUID());
            receipt.setTenantId(currentTenantId());
            receipt.setMessageId(messageId);
            receipt.setRecipientId(recipient.recipientId());
            receipt.setRecipientName(recipient.recipientName());
            receipt.setDeviceId(recipient.deviceId());
            receipt.setDeliveryStatus("PENDING");
            receipt.setCreateTime(now);
            receipt.setDelFlag("0");
            messageReceiptMapper.insert(receipt);
        }
    }

    private String targetName(String targetId, String targetType) {
        if ("DEVICE".equals(targetType)) {
            PatrolDevice device = targetId == null ? null : deviceMapper.selectById(targetId);
            return device == null ? blankToDefault(targetId, "-") : device.getDeviceName();
        }
        if ("ORG".equals(targetType)) {
            return blankToDefault(targetId, "-");
        }
        if ("SINGLE".equals(targetType) || "OFFICER".equals(targetType)) {
            PatrolDeviceBinding binding = activeBindingByBadgeNo(targetId);
            return binding == null ? blankToDefault(targetId, "-") : blankToDefault(binding.getNickName(), binding.getUserName());
        }
        return blankToDefault(targetId, "-");
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

    private PatrolDeviceBinding activeBindingByBadgeNo(String badgeNo) {
        if (badgeNo == null || badgeNo.isBlank()) {
            return null;
        }
        return deviceBindingMapper.selectList(new LambdaQueryWrapper<PatrolDeviceBinding>()
                .eq(PatrolDeviceBinding::getBadgeNo, badgeNo)
                .eq(PatrolDeviceBinding::getBindStatus, "BOUND")
                .orderByDesc(PatrolDeviceBinding::getBoundAt))
            .stream()
            .findFirst()
            .orElse(null);
    }

    private String currentOperator() {
        String username = LoginHelper.getUsername();
        return username == null || username.isBlank() ? "system" : username;
    }

    private String currentTenantId() {
        String tenantId = LoginHelper.getTenantId();
        return tenantId == null || tenantId.isBlank() ? "000000" : tenantId;
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "-";
        }
        return DATE_TIME_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    private Date parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            LocalDateTime dateTime = value.trim().length() <= 10
                ? LocalDate.parse(value.trim(), DATE_FORMATTER).atTime(23, 59, 59)
                : LocalDateTime.parse(value.trim(), DATE_TIME_FORMATTER);
            return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception ignored) {
            return null;
        }
    }

    private String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private String sizeText(long bytes) {
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024D);
        }
        return String.format("%.1f MB", bytes / 1024D / 1024D);
    }

    private String packageFormat(String fileName) {
        String lower = blankToDefault(fileName, "").toLowerCase();
        if (lower.endsWith(".zip")) {
            return "zip";
        }
        if (lower.endsWith(".ufw")) {
            return "ufw";
        }
        return "bin";
    }

    private String sha256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (DigestOutputStream output = new DigestOutputStream(OutputStream.nullOutputStream(), digest)) {
                file.getInputStream().transferTo(output);
            }
            return hex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new ServiceException("计算安装包SHA-256失败：" + e.getMessage());
        }
    }

    private String hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte item : bytes) {
            builder.append(String.format("%02x", item));
        }
        return builder.toString();
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }

    private int value(Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }

    private float value(Float value) {
        return value == null ? 0F : value;
    }

    private long value(Long value, long defaultValue) {
        return value == null ? defaultValue : value;
    }

    public record MetricVo(String label, String value, String note, String type) {
    }

    public record WorkItemVo(String id, String title, String officer, String source, String status, String level, String timeText) {
    }

    public record PlatformCapacityVo(Integer pilotOfficerCount, Integer pilotDeviceCount, Integer maxVideoChannels, String videoWallLayouts, String mapProvider, String algorithmProvider) {
    }

    public record DashboardSummaryVo(List<MetricVo> metrics, List<WorkItemVo> workItems, PlatformCapacityVo capacity) {
    }

    public record DeviceVo(String deviceId, String deviceName, String officerName, String badgeNo, String deptName, String onlineStatus, Integer batteryPercent, Integer signalBars, String firmwareVersion, String storageText, String workState, String lastOnlineTime) {
    }

    public record DeviceConfigVo(String deviceId, String deviceName, String officerName, String badgeNo, String deptName, DeviceCapabilitiesDto capabilities, DeviceWifiStateDto wifi, DeviceAdvancedSettingsDto settings, Boolean realtimeAudioSyncing, String lastMediaSyncAt) {
    }

    public record DeviceCommandBo(String command, String operatorId, String requestId) {
    }

    public record CommandResultVo(String commandId, String deviceId, String command, String status, String message) {
    }

    public record CommandLogVo(String commandId, String deviceId, String command, String operatorId, String status, String resultMessage, String sentAt, String ackAt) {
    }

    public record DeviceEventVo(String eventId, String deviceId, String eventType, String eventLevel, String eventTitle, String eventDetail, String occurredAt) {
    }

    public record DispatchChannelVo(String channelId, String deviceId, String officerName, String deptName, String state, String mode, Integer latencyMs, String locationText, Boolean talking) {
    }

    public record OfficerLocationVo(String badgeNo, String officerName, String deptName, String deviceId, BigDecimal latitude, BigDecimal longitude, String address, String onlineStatus, Integer batteryPercent, String reportedAt) {
    }

    public record TrackPointVo(String badgeNo, BigDecimal latitude, BigDecimal longitude, String address, String reportedAt) {
    }

    public record PatrolGeoPointVo(Double latitude, Double longitude) {
    }

    public record PatrolAreaVo(String areaId, String areaName, String teamId, String teamName, String ownerType, String userId, String badgeNo, List<PatrolGeoPointVo> boundary, List<PatrolGeoPointVo> route, String updatedAt) {
    }

    public record PatrolAreaBo(String areaId, String areaName, String teamId, String teamName, String ownerType, String userId, String badgeNo, List<PatrolGeoPointVo> boundary, List<PatrolGeoPointVo> route) {
    }

    public record AlertVo(String alertId, String alertType, String title, String targetName, String deviceId, String officerName, String locationText, String status, String level, String confidence, String occurredAt) {
    }

    public record AlertCloseBo(String result, String note, List<AlertAttachmentBo> attachments) {
    }

    public record AlertAttachmentBo(String clientFileId, String fileName, String mimeType, Long sizeBytes, String source, String localUri, String uploadIntent) {
    }

    public record AlertAttachmentVo(String attachmentId, String alertId, String clientFileId, String fileName, String mimeType, Long sizeBytes, String source, String localUri, String uploadIntent) {
    }

    public record AlertDispositionVo(String dispositionId, String alertId, String actionType, String actionResult, String operatorName, String note, Integer attachmentsCount, String occurredAt) {
    }

    public record AlertActionVo(String alertId, String nextStatus, String message) {
    }

    public record MediaVo(String fileId, String fileName, String mediaType, String deviceId, String officerName, String bizRef, String sizeText, String verifyStatus, String storagePath, String capturedAt, String sha256, String watermarkToken, String mimeType, Long fileSizeBytes, String contentUri) {
    }

    public record MediaActionVo(String fileId, String status, String message) {
    }

    public record CleanupResultVo(Integer cleaned, String message) {
    }

    public record DailyReportVo(String reportId, String missionId, String reportType, String deviceId, String operatorId, String officerName, String model, String backend, String generatedAt, String content, String documentUri, String documentName, String documentFormat, String mediaSelectionJson, String structuredContextJson, Boolean requiresHumanConfirmation, String status, String submitSource) {
    }

    public record DailyReportContentBo(String content) {
    }

    public record AppVersionVo(String versionId, Integer versionCode, String versionName, Boolean forceUpdate, String changelog, String downloadUrl, String sha256, String fileId, String status, String publishedAt) {
    }

    public record AppVersionPackageVo(String fileId, String fileName, String downloadUrl, String sha256, Long fileSizeBytes, String sizeText) {
    }

    public record AppVersionBo(Integer versionCode, String versionName, Boolean forceUpdate, String changelog, String downloadUrl, String sha256, String fileId) {
    }

    public record FirmwareVersionVo(String firmwareId, String deviceType, String vendor, String chipset, String deviceModel, String hardwareVersion, String firmwareType, Integer versionCode, String versionName, String minCurrentVersion, String maxCurrentVersion, Boolean forceUpdate, String changelog, String downloadUrl, String sha256, String fileId, Long fileSizeBytes, String packageFormat, String upgradeMode, String grayScope, String grayTargets, String status, String publishedAt, String remark) {
    }

    public record FirmwarePackageVo(String fileId, String fileName, String downloadUrl, String sha256, Long fileSizeBytes, String sizeText, String packageFormat) {
    }

    public record FirmwareVersionBo(String deviceType, String vendor, String chipset, String deviceModel, String hardwareVersion, String firmwareType, Integer versionCode, String versionName, String minCurrentVersion, String maxCurrentVersion, Boolean forceUpdate, String changelog, String downloadUrl, String sha256, String fileId, Long fileSizeBytes, String packageFormat, String upgradeMode, String grayScope, String grayTargets, String remark) {
    }

    public record FirmwareUpgradeTaskVo(String taskId, String deviceId, String firmwareId, String operatorId, String fromVersion, String toVersion, String status, Float progress, String errorCode, String errorMessage, String startedAt, String finishedAt) {
    }

    public record SosVo(String sosId, String officerName, String badgeNo, String deptName, String deviceId, String locationText, Double latitude, Double longitude, Float accuracyMeters, String status, String disposition, Boolean recordingAudio, Integer backupEtaMinutes, String createdAt) {
    }

    public record SosActionVo(String sosId, String status, String message) {
    }

    public record SosTimelineVo(String dispositionId, String sosId, String actionType, String actionResult, String operatorName, String note, String contactName, String contactPhone, String attachmentFileId, String attachmentFileName, Integer backupEtaMinutes, String occurredAt) {
    }

    public record SosBackupBo(String contactName, String contactPhone, Integer backupEtaMinutes, String note) {
    }

    public record SosContactBo(String contactName, String contactPhone, String note) {
    }

    public record SosNoteBo(String note) {
    }

    public record MessageBo(String targetId, String targetType, String title, String content) {
    }

    public record MessageResultVo(String messageId, String targetId, String status, String sentAt) {
    }

    public record MessageVo(String messageId, String title, String content, String targetType, String targetName, String channel, String status, Integer deliveredCount, Integer pendingCount, Integer readCount, Integer totalCount, String sentAt) {
    }

    public record MessageReceiptVo(String receiptId, String messageId, String recipientId, String recipientName, String deviceId, String deliveryStatus, String deliveredAt, String readAt, String lastPullAt) {
    }

    private record MessageRecipient(String recipientId, String recipientName, String deviceId) {
    }

    public record ControlPersonVo(String controlId, String name, String category, String riskLevel, String status, String source, String expiresAt, String faceImageUrl, String faceImageSha256, String faceUpdatedAt, Boolean hasFaceImage) {
    }

    public record ControlVehicleVo(String controlId, String plateNo, String vehicleDesc, String riskLevel, String status, String source, String expiresAt) {
    }

    public record ControlPersonBo(String name, String category, String idCardNo, String riskLevel, String expiresAt, String remark, String faceImageUrl, String faceImageSha256) {
    }

    public record ControlPersonFaceImageBo(String faceImageUrl, String faceImageSha256) {
    }

    public record ControlPersonFaceImageVo(String controlId, String faceImageUrl, String faceImageSha256, String faceUpdatedAt) {
    }

    public record ControlVehicleBo(String plateNo, String vehicleDesc, String vehicleType, String riskLevel, String expiresAt, String remark) {
    }

    public record StatusBo(String status) {
    }

    public record ControlStatusVo(String controlId, String status, String message) {
    }

    public record ImportResultVo(String taskId, Integer total, Integer success, Integer failed, String message, List<ImportErrorVo> errors) {
    }

    public record ImportErrorVo(Integer rowNo, String reason) {
    }

    public record StatisticsOverviewVo(List<MetricVo> metrics, List<TrendPointVo> alertTrend, List<RankingItemVo> deviceRiskRanking, List<RankingItemVo> dispositionStats) {
    }

    public record TrendPointVo(String date, Integer alerts, Integer sos, Integer media, Integer dispatchSessions) {
    }

    public record RankingItemVo(String name, Integer value, String note) {
    }

    public record AuditLogVo(String logId, String logType, String operatorName, String action, String resource, String result, String ipAddress, String traceId, String occurredAt) {
    }

    public record SystemHealthVo(String componentName, String status, String instance, String detail) {
    }

    public record IntegrationConfigVo(String integrationKey, String name, String status, String protocol, String note) {
    }
}
