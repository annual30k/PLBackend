package org.dromara.patrol.controller;

import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * PatrolLink command platform bootstrap API.
 * <p>
 * The first implementation keeps data in deterministic DTOs so the Vue command
 * console and PL2Android contract can be developed before database schemas,
 * MinIO buckets, streaming nodes and third-party algorithm services are wired.
 */
@RestController
@RequestMapping("/patrol")
public class PatrolController {

    @GetMapping("/dashboard/summary")
    public R<DashboardSummaryVo> dashboard() {
        return R.ok(new DashboardSummaryVo(
            List.of(
                new MetricVo("在线警员", "84", "100 名试点警员", "success"),
                new MetricVo("在线设备", "126", "150 台执法设备", "primary"),
                new MetricVo("视频会话", "8", "最大支持 16 路", "warning"),
                new MetricVo("未处置预警", "5", "人员 3 / 车辆 2", "danger"),
                new MetricVo("SOS 求助", "1", "增援派遣中", "danger"),
                new MetricVo("今日媒体", "312", "约 48.6 GB", "info")
            ),
            List.of(
                new WorkItemVo("alert-20260514001", "人员布控比中", "张建国", "执法耳机 HEADSET-012", "未处置", "CRITICAL", "2 分钟前"),
                new WorkItemVo("sos-20260514001", "一键求助", "李明", "人民路与解放路口", "增援中", "CRITICAL", "5 分钟前"),
                new WorkItemVo("device-20260514001", "设备低电量", "王强", "HEADSET-038 电量 12%", "待确认", "WARNING", "8 分钟前")
            ),
            new PlatformCapacityVo(100, 150, 16, "2/4/8/12/16 视频墙布局", "高德地图", "第三方人脸比对/车牌 OCR")
        ));
    }

    @GetMapping("/devices")
    public R<List<DeviceVo>> devices() {
        return R.ok(List.of(
            new DeviceVo("HEADSET-012", "执法耳机 012", "张建国", "A01358", "巡特警一大队", "ONLINE", 86, 4, "v1.3.5", "32GB/128GB", "VIDEO_STREAMING", "2026-05-14 08:21:11"),
            new DeviceVo("HEADSET-038", "执法耳机 038", "王强", "A01933", "交警二中队", "ONLINE", 12, 3, "v1.3.5", "96GB/128GB", "LOW_BATTERY", "2026-05-14 08:21:09"),
            new DeviceVo("HEADSET-071", "执法耳机 071", "赵敏", "A01762", "巡特警二大队", "OFFLINE", 0, 0, "v1.3.4", "41GB/128GB", "OFFLINE", "2026-05-13 21:44:02")
        ));
    }

    @PostMapping("/devices/{deviceId}/commands")
    public R<CommandResultVo> command(@PathVariable String deviceId, @RequestBody DeviceCommandBo command) {
        return R.ok(new CommandResultVo("cmd-" + System.currentTimeMillis(), deviceId, command.command(), "ACCEPTED", "指令已进入下发队列"));
    }

    @GetMapping("/dispatch/channels")
    public R<List<DispatchChannelVo>> dispatchChannels() {
        return R.ok(List.of(
            new DispatchChannelVo("CH-01", "HEADSET-012", "张建国", "巡特警一大队", "LIVE", "低延迟", 318, "人民广场北侧", true),
            new DispatchChannelVo("CH-02", "HEADSET-038", "王强", "交警二中队", "LIVE", "证据质量", 642, "解放路口", false),
            new DispatchChannelVo("CH-03", "HEADSET-055", "陈宇", "派出所网格组", "CONNECTING", "均衡", 0, "文化路商圈", false)
        ));
    }

    @PostMapping("/dispatch/sessions")
    public R<DispatchSessionVo> createDispatchSession(@RequestBody DispatchSessionBo bo) {
        return R.ok(new DispatchSessionVo(
            "dispatch-" + System.currentTimeMillis(),
            bo.deviceId(),
            bo.mode(),
            "webrtc://media.patrollink.local/live/" + bo.deviceId(),
            "CREATED"
        ));
    }

    @DeleteMapping("/dispatch/sessions/{sessionId}")
    public R<DispatchActionVo> closeDispatchSession(@PathVariable String sessionId) {
        return R.ok(new DispatchActionVo(sessionId, "CLOSED", "调度会话已关闭，设备推流状态等待 App 回执"));
    }

    @PostMapping("/dispatch/sessions/{sessionId}/snapshot")
    public R<MediaVo> snapshot(@PathVariable String sessionId) {
        return R.ok(new MediaVo(
            "MF-SNAPSHOT-" + System.currentTimeMillis(),
            sessionId + "_snapshot.jpg",
            "PHOTO",
            "HEADSET-012",
            "张建国",
            sessionId,
            "1.6 MB",
            "PENDING",
            "MinIO/patrol-evidence",
            "2026-05-14 09:20:18"
        ));
    }

    @PostMapping("/dispatch/sessions/{sessionId}/talk/start")
    public R<TalkSessionVo> startTalk(@PathVariable String sessionId) {
        return R.ok(new TalkSessionVo("talk-" + System.currentTimeMillis(), sessionId, "ACTIVE", "wss://media.patrollink.local/talk/" + sessionId));
    }

    @PostMapping("/dispatch/sessions/{sessionId}/talk/stop")
    public R<TalkSessionVo> stopTalk(@PathVariable String sessionId) {
        return R.ok(new TalkSessionVo("talk-" + System.currentTimeMillis(), sessionId, "CLOSED", ""));
    }

    @GetMapping("/map/officers")
    public R<List<OfficerLocationVo>> officerLocations() {
        return R.ok(List.of(
            new OfficerLocationVo("A01358", "张建国", "巡特警一大队", "HEADSET-012", new BigDecimal("39.908722"), new BigDecimal("116.397499"), "人民广场北侧", "ONLINE", 86, "2026-05-14 08:21:12"),
            new OfficerLocationVo("A01933", "王强", "交警二中队", "HEADSET-038", new BigDecimal("39.907612"), new BigDecimal("116.403181"), "解放路口", "ONLINE", 12, "2026-05-14 08:21:10"),
            new OfficerLocationVo("A01762", "赵敏", "巡特警二大队", "HEADSET-071", new BigDecimal("39.912301"), new BigDecimal("116.391084"), "文化路商圈", "OFFLINE", 0, "2026-05-13 21:44:02")
        ));
    }

    @GetMapping("/map/officers/{badgeNo}/track")
    public R<List<TrackPointVo>> officerTrack(@PathVariable String badgeNo) {
        return R.ok(List.of(
            new TrackPointVo(badgeNo, new BigDecimal("39.904812"), new BigDecimal("116.391204"), "巡区入口", "2026-05-14 08:00:00"),
            new TrackPointVo(badgeNo, new BigDecimal("39.906402"), new BigDecimal("116.394018"), "商业街西侧", "2026-05-14 08:08:30"),
            new TrackPointVo(badgeNo, new BigDecimal("39.908722"), new BigDecimal("116.397499"), "人民广场北侧", "2026-05-14 08:21:12")
        ));
    }

    @GetMapping("/alerts")
    public R<List<AlertVo>> alerts() {
        return R.ok(List.of(
            new AlertVo("AL-20260514-001", "PERSON", "人员布控比中", "李某某", "HEADSET-012", "张建国", "人民广场北侧", "PENDING", "CRITICAL", "96.8%", "2026-05-14 08:19:21"),
            new AlertVo("AL-20260514-002", "VEHICLE", "重点车辆比中", "京A12345", "HEADSET-038", "王强", "解放路口", "HANDLING", "WARNING", "92.1%", "2026-05-14 08:12:42"),
            new AlertVo("AL-20260514-003", "PERSON", "人员布控比中", "王某某", "HEADSET-055", "陈宇", "文化路商圈", "CLOSED", "INFO", "88.4%", "2026-05-14 07:58:11")
        ));
    }

    @PostMapping("/alerts/{alertId}/ack")
    public R<AlertActionVo> acknowledgeAlert(@PathVariable String alertId) {
        return R.ok(new AlertActionVo(alertId, "HANDLING", "预警已确认，进入处置中"));
    }

    @PostMapping("/alerts/{alertId}/close")
    public R<AlertActionVo> closeAlert(@PathVariable String alertId, @RequestBody AlertCloseBo bo) {
        return R.ok(new AlertActionVo(alertId, "CLOSED", "处置结果已提交：" + bo.result()));
    }

    @GetMapping("/media")
    public R<List<MediaVo>> media() {
        return R.ok(List.of(
            new MediaVo("MF-001", "现场照片_081921.jpg", "PHOTO", "HEADSET-012", "张建国", "AL-20260514-001", "2.8 MB", "VERIFIED", "MinIO/patrol-evidence", "2026-05-14 08:19:21"),
            new MediaVo("MF-002", "视频片段_081242.mp4", "VIDEO", "HEADSET-038", "王强", "AL-20260514-002", "128.4 MB", "HASHING", "MinIO/patrol-media", "2026-05-14 08:12:42"),
            new MediaVo("MF-003", "SOS录音_080511.aac", "AUDIO", "HEADSET-066", "刘洋", "SOS-20260514-001", "4.1 MB", "UPLOADING", "MinIO/patrol-sos", "2026-05-14 08:05:11")
        ));
    }

    @GetMapping("/sos")
    public R<List<SosVo>> sos() {
        return R.ok(List.of(
            new SosVo("SOS-20260514-001", "刘洋", "A01666", "派出所网格组", "HEADSET-066", "人民路与解放路口", "ACTIVE", "增援派遣中", true, 4, "2026-05-14 08:05:11")
        ));
    }

    @PostMapping("/messages/send")
    public R<MessageResultVo> sendMessage(@RequestBody MessageBo bo) {
        return R.ok(new MessageResultVo("msg-" + System.currentTimeMillis(), bo.targetId(), "SENT", LocalDateTime.now().toString()));
    }

    @GetMapping("/messages")
    public R<List<MessageVo>> messages() {
        return R.ok(List.of(
            new MessageVo("MSG-001", "SINGLE", "A01358", "张建国", "请前往人民广场北侧支援", "READ", "2026-05-14 08:22:10"),
            new MessageVo("MSG-002", "ORG", "巡特警一大队", "巡特警一大队", "重点人员预警升级，注意联动盘查", "SENT", "2026-05-14 08:18:42"),
            new MessageVo("MSG-003", "DEVICE", "HEADSET-038", "执法耳机 038", "设备低电量，请更换备用设备", "UNREAD", "2026-05-14 08:14:03")
        ));
    }

    @GetMapping("/control/persons")
    public R<List<ControlPersonVo>> controlPersons() {
        return R.ok(List.of(
            new ControlPersonVo("CP-001", "李某某", "重点关注", "HIGH", "ENABLED", "第三方重点人员库", "2026-06-30"),
            new ControlPersonVo("CP-002", "王某某", "临控人员", "MEDIUM", "ENABLED", "平台导入", "2026-05-31")
        ));
    }

    @PostMapping("/control/persons")
    public R<ControlPersonVo> createControlPerson(@RequestBody ControlPersonBo bo) {
        return R.ok(new ControlPersonVo("CP-" + System.currentTimeMillis(), bo.name(), bo.category(), bo.riskLevel(), "ENABLED", "平台录入", bo.expiresAt()));
    }

    @PostMapping("/control/persons/import")
    public R<ImportResultVo> importControlPersons() {
        return R.ok(new ImportResultVo("IMPORT-" + System.currentTimeMillis(), 128, 126, 2, "人员布控导入任务已完成"));
    }

    @PatchMapping("/control/persons/{controlId}/status")
    public R<ControlStatusVo> updateControlPersonStatus(@PathVariable String controlId, @RequestBody StatusBo bo) {
        return R.ok(new ControlStatusVo(controlId, bo.status(), "人员布控状态已更新"));
    }

    @GetMapping("/control/vehicles")
    public R<List<ControlVehicleVo>> controlVehicles() {
        return R.ok(List.of(
            new ControlVehicleVo("CV-001", "京A12345", "黑色 SUV", "HIGH", "ENABLED", "第三方重点车辆库", "2026-06-30"),
            new ControlVehicleVo("CV-002", "京B67890", "白色轿车", "MEDIUM", "DISABLED", "平台录入", "2026-05-31")
        ));
    }

    @PostMapping("/control/vehicles")
    public R<ControlVehicleVo> createControlVehicle(@RequestBody ControlVehicleBo bo) {
        return R.ok(new ControlVehicleVo("CV-" + System.currentTimeMillis(), bo.plateNo(), bo.vehicleDesc(), bo.riskLevel(), "ENABLED", "平台录入", bo.expiresAt()));
    }

    @PatchMapping("/control/vehicles/{controlId}/status")
    public R<ControlStatusVo> updateControlVehicleStatus(@PathVariable String controlId, @RequestBody StatusBo bo) {
        return R.ok(new ControlStatusVo(controlId, bo.status(), "车辆布控状态已更新"));
    }

    @GetMapping("/statistics/overview")
    public R<StatisticsOverviewVo> statisticsOverview() {
        return R.ok(new StatisticsOverviewVo(
            List.of(
                new MetricVo("警力在线率", "84%", "84/100", "success"),
                new MetricVo("设备在线率", "84%", "126/150", "primary"),
                new MetricVo("误报率", "7.2%", "近 7 日", "warning"),
                new MetricVo("平均处置时长", "6.8 分钟", "预警 + SOS", "info")
            ),
            List.of(
                new TrendPointVo("05-08", 31, 2, 18),
                new TrendPointVo("05-09", 36, 1, 23),
                new TrendPointVo("05-10", 42, 3, 29),
                new TrendPointVo("05-11", 28, 2, 26),
                new TrendPointVo("05-12", 47, 4, 35),
                new TrendPointVo("05-13", 51, 2, 41),
                new TrendPointVo("05-14", 19, 1, 12)
            ),
            List.of(
                new RankingItemVo("HEADSET-038", "低电量次数", 6),
                new RankingItemVo("HEADSET-071", "离线次数", 4),
                new RankingItemVo("HEADSET-055", "指令失败", 3)
            )
        ));
    }

    @GetMapping("/system/audit-logs")
    public R<List<AuditLogVo>> auditLogs() {
        return R.ok(List.of(
            new AuditLogVo("AUD-001", "admin", "发起视频调度", "DISPATCH_CREATE", "HEADSET-012", "SUCCESS", "2026-05-14 08:21:18"),
            new AuditLogVo("AUD-002", "admin", "确认预警", "ALERT_ACK", "AL-20260514-001", "SUCCESS", "2026-05-14 08:20:03"),
            new AuditLogVo("AUD-003", "test", "下载证据文件", "MEDIA_DOWNLOAD", "MF-001", "SUCCESS", "2026-05-14 08:19:51")
        ));
    }

    @GetMapping("/system/health")
    public R<List<SystemHealthVo>> systemHealth() {
        return R.ok(List.of(
            new SystemHealthVo("业务 API", "UP", "1 个实例", "10.885s 启动完成"),
            new SystemHealthVo("MySQL", "UP", "ry-vue", "连接池 master 正常"),
            new SystemHealthVo("Redis", "UP", "Redisson", "9 个连接已初始化"),
            new SystemHealthVo("MinIO", "UP", "ruoyi/patrol-media/patrol-evidence/patrol-sos", "桶初始化完成"),
            new SystemHealthVo("流媒体节点", "RESERVED", "ZLMediaKit/SRS/Janus", "待接入")
        ));
    }

    @GetMapping("/integration/configs")
    public R<List<IntegrationConfigVo>> integrationConfigs() {
        return R.ok(List.of(
            new IntegrationConfigVo("FACE_MATCH", "第三方人脸比对", "RESERVED", "HTTP API / MQ", "接收比中结果并转预警"),
            new IntegrationConfigVo("PLATE_OCR", "第三方车牌 OCR", "RESERVED", "HTTP API / MQ", "接收车牌结构化结果"),
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
            "algorithm", "第三方人脸比对与车牌 OCR",
            "policeCallIntegration", "预留 110 接处警平台接口"
        ));
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

    public record DeviceCommandBo(String command, String operatorId, String requestId) {
    }

    public record CommandResultVo(String commandId, String deviceId, String command, String status, String message) {
    }

    public record DispatchChannelVo(String channelId, String deviceId, String officerName, String deptName, String state, String mode, Integer latencyMs, String locationText, Boolean talking) {
    }

    public record DispatchSessionBo(String deviceId, String mode) {
    }

    public record DispatchSessionVo(String sessionId, String deviceId, String mode, String relayUrl, String state) {
    }

    public record DispatchActionVo(String sessionId, String nextState, String message) {
    }

    public record TalkSessionVo(String talkId, String sessionId, String state, String talkUrl) {
    }

    public record OfficerLocationVo(String badgeNo, String officerName, String deptName, String deviceId, BigDecimal latitude, BigDecimal longitude, String address, String onlineStatus, Integer batteryPercent, String reportedAt) {
    }

    public record TrackPointVo(String badgeNo, BigDecimal latitude, BigDecimal longitude, String address, String reportedAt) {
    }

    public record AlertVo(String alertId, String alertType, String title, String targetName, String deviceId, String officerName, String locationText, String status, String level, String confidence, String occurredAt) {
    }

    public record AlertCloseBo(String result, String note) {
    }

    public record AlertActionVo(String alertId, String nextStatus, String message) {
    }

    public record MediaVo(String fileId, String fileName, String mediaType, String deviceId, String officerName, String bizRef, String sizeText, String verifyStatus, String storagePath, String capturedAt) {
    }

    public record SosVo(String sosId, String officerName, String badgeNo, String deptName, String deviceId, String locationText, String status, String disposition, Boolean recordingAudio, Integer backupEtaMinutes, String createdAt) {
    }

    public record MessageBo(String targetId, String targetType, String content) {
    }

    public record MessageResultVo(String messageId, String targetId, String status, String sentAt) {
    }

    public record MessageVo(String messageId, String targetType, String targetId, String targetName, String content, String deliveryStatus, String sentAt) {
    }

    public record ControlPersonVo(String controlId, String name, String category, String riskLevel, String status, String source, String expiresAt) {
    }

    public record ControlVehicleVo(String controlId, String plateNo, String vehicleDesc, String riskLevel, String status, String source, String expiresAt) {
    }

    public record ControlPersonBo(String name, String category, String riskLevel, String expiresAt) {
    }

    public record ControlVehicleBo(String plateNo, String vehicleDesc, String riskLevel, String expiresAt) {
    }

    public record StatusBo(String status) {
    }

    public record ControlStatusVo(String controlId, String status, String message) {
    }

    public record ImportResultVo(String taskId, Integer total, Integer success, Integer failed, String message) {
    }

    public record StatisticsOverviewVo(List<MetricVo> metrics, List<TrendPointVo> alertTrend, List<RankingItemVo> deviceRanking) {
    }

    public record TrendPointVo(String date, Integer alertCount, Integer sosCount, Integer mediaCount) {
    }

    public record RankingItemVo(String name, String metric, Integer value) {
    }

    public record AuditLogVo(String logId, String operatorName, String actionName, String actionCode, String bizRef, String result, String operatedAt) {
    }

    public record SystemHealthVo(String componentName, String status, String instance, String detail) {
    }

    public record IntegrationConfigVo(String integrationKey, String name, String status, String protocol, String note) {
    }
}
