package org.dromara.patrol.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * PL2Android REST contract bootstrap API.
 * <p>
 * These endpoints mirror the current Android client DTOs so mobile, web and
 * backend work can be integrated before persistence and device gateways are
 * wired into the mature RuoYi-Vue-Plus authentication and data layers.
 */
@SaIgnore
@RestController
@RequestMapping("/api/v1")
public class PatrolAppApiController {

    private static final String DEVICE_ID = "HEADSET_001";
    private static final long MOCK_TIME = 1715832000L;
    private DeviceStatusDto device = deviceStatus(DEVICE_ID, "IDLE", false);
    private final List<AlertDto> alerts = new ArrayList<>(List.of(
        new AlertDto("AL-99824-03", "非法侵入监测", "CRITICAL", "PENDING", "14:32", "西三区 4号围墙 节点B", "CAM-042", "围墙节点 B 检测到人员越界，耳机端已同步 12 秒现场视频片段。", "98.4%"),
        new AlertDto("AL-99824-04", "未识别车辆靠近", "WARNING", "PENDING", "14:38", "北侧周界入口", "RFID-09", "车牌识别失败，建议现场复核并记录车辆去向。", "91.2%"),
        new AlertDto("AL-99821-11", "夜间巡查异常声源", "INFO", "CLOSED", "13:22", "核心商务区 CBD-North", DEVICE_ID, "环境音频超过阈值，现场确认无风险。", "74.8%")
    ));
    private List<MediaFileDto> media = new ArrayList<>(List.of(
        new MediaFileDto("VID-042", "CAM_04_A", "VIDEO", "14:22:05", "84.1 MB", "04:12", true, "DEVICE", "IDLE", 0F, null),
        new MediaFileDto("IMG-8821", "IMG_8821", "PHOTO", "14:45:12", "2.4 MB", null, true, "DEVICE", "DONE", 1F, null),
        new MediaFileDto("AUD-318", "VOICE_318", "AUDIO", "14:50:02", "8.6 MB", "03:55", true, "PHONE", "IDLE", 0F, null),
        new MediaFileDto("VID-051", "PATROL_051", "VIDEO", "15:02:18", "126 MB", "08:12", false, "PHONE", "IDLE", 0F, null)
    ));

    @PostMapping("/auth/login")
    public ApiEnvelope<AuthSessionDto> login(@RequestBody LoginRequestDto request) {
        String account = normalizeAccount(request.account());
        return ok(new AuthSessionDto(
            "mock-access-" + account,
            "mock-refresh-" + account,
            7200L,
            "Bearer"
        ));
    }

    @PostMapping("/auth/refresh")
    public ApiEnvelope<AuthSessionDto> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.getOrDefault("refreshToken", "mock-refresh-police");
        return ok(new AuthSessionDto(
            refreshToken.replace("refresh", "access"),
            refreshToken,
            7200L,
            "Bearer"
        ));
    }

    @GetMapping("/users/me")
    public ApiEnvelope<UserProfileDto> currentUser() {
        return ok(new UserProfileDto(
            "U-9527",
            "张警官",
            "POLICE_9527",
            "第一巡逻支队",
            "+86 138-0000-9527",
            "zhang.police@city.gov.cn",
            "福州温泉公园",
            "05:24:12",
            "巡逻组 A-42 | 温泉公园重点巡区",
            "0x4F2A"
        ));
    }

    @GetMapping("/devices/scan")
    public ApiEnvelope<List<ScannedDeviceDto>> scanDevices() {
        return ok(List.of(
            new ScannedDeviceDto("HEADSET_001", "ForceLink-H1", 4, "0000-pl2-ble-control", true, "2C:4A:91:3F:8B:02", "HEADSET"),
            new ScannedDeviceDto("RECORDER_A5", "ForceLink-A5", 3, "0000-pl2-ble-control", false, "4F:02:8C:76:A1:19", "RECORDER"),
            new ScannedDeviceDto("SENSOR_S9", "ForceLink-S9", 2, "0000-pl2-ble-control", false, "1E:BD:55:0A:44:71", "SENSOR"),
            new ScannedDeviceDto("GLASSES_G1", "ForceLink-G1", 4, "0000-pl2-ble-control", false, "6B:13:9E:41:D7:50", "GLASSES")
        ));
    }

    @PostMapping("/devices/{deviceId}/bind")
    public synchronized ApiEnvelope<DeviceStatusDto> bindDevice(@PathVariable String deviceId) {
        device = new DeviceStatusDto(deviceId, "ForceLink-H1", true, 88, 4, "02:45:12", 42.5F, 128F, "v1.2.4", "IDLE", false, true);
        return ok(device);
    }

    @PostMapping("/devices/{deviceId}/commands")
    public synchronized ApiEnvelope<DeviceStatusDto> sendDeviceCommand(@PathVariable String deviceId, @RequestBody DeviceCommandRequestDto request) {
        String recordingStatus = device.recordingStatus();
        boolean talking = device.talking();
        if ("START_RECORD".equals(request.command())) {
            recordingStatus = "RECORDING";
        } else if ("STOP_RECORD".equals(request.command())) {
            recordingStatus = "IDLE";
        } else if ("START_TALK".equals(request.command())) {
            talking = true;
        } else if ("STOP_TALK".equals(request.command())) {
            talking = false;
        }
        device = new DeviceStatusDto(deviceId, "ForceLink-H1", true, device.batteryPercent(), device.signalBars(), device.onlineDuration(), device.storageUsedGb(), device.storageTotalGb(), device.firmwareVersion(), recordingStatus, talking, true);
        return ok(device);
    }

    @GetMapping("/alerts")
    public ApiEnvelope<PageEnvelope<AlertDto>> alerts(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        List<AlertDto> items = List.copyOf(alerts);
        return ok(page(items, page, pageSize));
    }

    @PostMapping("/alerts/{alertId}/ack")
    public synchronized ApiEnvelope<AlertDto> acknowledgeAlert(@PathVariable String alertId) {
        return ok(updateAlert(alertId, "HANDLING", null));
    }

    @PostMapping("/alerts/{alertId}/close")
    public synchronized ApiEnvelope<AlertDto> closeAlert(@PathVariable String alertId, @RequestBody AlertCloseRequestDto request) {
        return ok(updateAlert(alertId, "CLOSED", request.result() + "：" + request.note()));
    }

    @GetMapping("/media")
    public ApiEnvelope<PageEnvelope<MediaFileDto>> mediaFiles(
        @RequestParam(defaultValue = "DEVICE") String side,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "50") int pageSize) {
        List<MediaFileDto> items = media.stream()
            .filter(item -> item.storageSide().equalsIgnoreCase(side))
            .toList();
        return ok(page(items, page, pageSize));
    }

    @PostMapping("/media/{fileId}/transfer")
    public synchronized List<ApiEnvelope<MediaFileDto>> transferMedia(@PathVariable String fileId, @RequestBody TransferRequestDto request) {
        MediaFileDto original = media.stream()
            .filter(item -> item.fileId().equals(fileId) && ("PHONE_SANDBOX".equals(request.target()) ? "DEVICE".equals(item.storageSide()) : true))
            .findFirst()
            .orElseGet(() -> new MediaFileDto(fileId, fileId, "VIDEO", "15:30:00", "0 MB", null, false, "DEVICE", "IDLE", 0F, null));
        String side = "PHONE_SANDBOX".equals(request.target()) ? "PHONE" : original.storageSide();
        MediaFileDto hashing = withTransfer(original, side, "HASHING", 0.1F, false);
        MediaFileDto uploading = withTransfer(original, side, "UPLOADING", 0.55F, false);
        MediaFileDto verifying = withTransfer(original, side, "VERIFYING", 0.9F, false);
        MediaFileDto done = withTransfer(original, side, "DONE", 1F, true);
        MediaFileDto stored = "PHONE_SANDBOX".equals(request.target())
            ? withTransfer(original, side, "IDLE", 0F, true)
            : done;
        media = upsertMedia(stored);
        return List.of(
            ok(hashing),
            ok(uploading),
            ok(verifying),
            ok(done)
        );
    }

    @DeleteMapping("/media/{fileId}")
    public synchronized ApiEnvelope<Boolean> deleteMedia(@PathVariable String fileId, @RequestParam(defaultValue = "") String side) {
        int before = media.size();
        media = media.stream()
            .filter(item -> !(item.fileId().equals(fileId) && (side.isBlank() || item.storageSide().equalsIgnoreCase(side))))
            .toList();
        return ok(media.size() < before);
    }

    @PostMapping("/media/{fileId}/verify")
    public synchronized ApiEnvelope<Boolean> verifyMedia(@PathVariable String fileId) {
        boolean found = media.stream().anyMatch(item -> item.fileId().equals(fileId));
        if (found) {
            media = media.stream()
                .map(item -> item.fileId().equals(fileId)
                    ? new MediaFileDto(item.fileId(), item.fileName(), item.mediaType(), item.capturedAt(), item.sizeText(), item.durationText(), true, item.storageSide(), item.transferStatus(), item.progress(), item.contentUri())
                    : item)
                .toList();
        }
        return ok(found);
    }

    @PostMapping("/realtime/heartbeat")
    public synchronized ApiEnvelope<HeartbeatAckDto> heartbeat(@RequestBody HeartbeatRequestDto request) {
        device = new DeviceStatusDto(request.deviceId(), device.deviceName(), request.online(), request.batteryPercent(), request.signalBars(), device.onlineDuration(), device.storageUsedGb(), device.storageTotalGb(), device.firmwareVersion(), request.recordingStatus(), device.talking(), true);
        return ok(new HeartbeatAckDto(request.online(), MOCK_TIME, 15));
    }

    @PostMapping("/stream/start")
    public ApiEnvelope<StreamRelayStateDto> startStream(@RequestBody StreamRelayRequestDto request) {
        return ok(new StreamRelayStateDto(
            "RELAYING",
            "webrtc://patrollink.local/live/" + request.deviceId(),
            "LOW_LATENCY".equals(request.mode()) ? 80 : 160
        ));
    }

    @PostMapping("/stream/stop")
    public ApiEnvelope<StreamRelayStateDto> stopStream() {
        return ok(new StreamRelayStateDto("IDLE", null, null));
    }

    @GetMapping("/patrol/areas/current")
    public ApiEnvelope<PatrolAreaDto> currentPatrolArea() {
        return ok(new PatrolAreaDto(
            "AREA-FZ-WQ-001",
            "福州温泉公园重点巡区",
            "TEAM-A-42",
            "巡逻组 A-42",
            List.of(
                new PatrolGeoPointDto(26.10295, 119.30485),
                new PatrolGeoPointDto(26.10335, 119.31010),
                new PatrolGeoPointDto(26.10020, 119.31115),
                new PatrolGeoPointDto(26.09795, 119.30910),
                new PatrolGeoPointDto(26.09815, 119.30465)
            ),
            List.of(
                new PatrolGeoPointDto(26.09875, 119.30495),
                new PatrolGeoPointDto(26.10020, 119.30655),
                new PatrolGeoPointDto(26.10058, 119.30771),
                new PatrolGeoPointDto(26.10155, 119.30900),
                new PatrolGeoPointDto(26.10255, 119.30795)
            )
        ));
    }

    @PostMapping("/sos/activate")
    public ApiEnvelope<SosEventDto> activateSos(@RequestBody GpsLocationDto location) {
        return ok(new SosEventDto("SOS-1715832000", "ACTIVE", "紧急上报已激活", location, true, 4));
    }

    @PostMapping("/sos/cancel")
    public ApiEnvelope<SosEventDto> cancelSos() {
        return ok(new SosEventDto("SOS-CANCEL", "CANCELLED", "紧急上报已取消", null, false, null));
    }

    private DeviceStatusDto deviceStatus(String deviceId, String recordingStatus, boolean talking) {
        return new DeviceStatusDto(
            deviceId,
            "ForceLink-H1",
            true,
            88,
            4,
            "02:45:12",
            42.5F,
            128F,
            "v1.2.4",
            recordingStatus,
            talking,
            true
        );
    }

    private AlertDto fallbackAlert(String alertId, String status) {
        return new AlertDto(alertId, "预警事件", "INFO", status, "15:00", "未知位置", DEVICE_ID, "事件状态已更新。", "0%");
    }

    private MediaFileDto withTransfer(MediaFileDto original, String side, String status, float progress, boolean verified) {
        return new MediaFileDto(
            original.fileId(),
            original.fileName(),
            original.mediaType(),
            original.capturedAt(),
            original.sizeText(),
            original.durationText(),
            verified || original.sha256Verified(),
            side,
            status,
            progress,
            original.contentUri()
        );
    }

    private AlertDto updateAlert(String alertId, String status, String description) {
        for (int index = 0; index < alerts.size(); index++) {
            AlertDto item = alerts.get(index);
            if (item.alertId().equals(alertId)) {
                AlertDto updated = new AlertDto(item.alertId(), item.title(), item.level(), status, item.occurredAt(), item.locationText(), item.source(), description == null ? item.description() : description, item.confidence());
                alerts.set(index, updated);
                return updated;
            }
        }
        AlertDto fallback = fallbackAlert(alertId, status);
        alerts.add(fallback);
        return fallback;
    }

    private List<MediaFileDto> upsertMedia(MediaFileDto file) {
        boolean replaced = false;
        List<MediaFileDto> updated = new ArrayList<>();
        for (MediaFileDto item : media) {
            if (item.fileId().equals(file.fileId()) && item.storageSide().equals(file.storageSide())) {
                updated.add(file);
                replaced = true;
            } else {
                updated.add(item);
            }
        }
        if (!replaced) {
            updated.add(file);
        }
        return updated;
    }

    private <T> PageEnvelope<T> page(List<T> items, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(pageSize, 1);
        int from = Math.min((safePage - 1) * safePageSize, items.size());
        int to = Math.min(from + safePageSize, items.size());
        return new PageEnvelope<>(items.subList(from, to), safePage, safePageSize, items.size(), to < items.size());
    }

    private String normalizeAccount(String account) {
        if (account == null || account.isBlank()) {
            return "police";
        }
        return account.trim();
    }

    private <T> ApiEnvelope<T> ok(T data) {
        return new ApiEnvelope<>(200, "OK", data, UUID.randomUUID().toString(), MOCK_TIME);
    }

    public record ApiEnvelope<T>(int code, String message, T data, String traceId, long timestamp) {
    }

    public record PageEnvelope<T>(List<T> items, int page, int pageSize, long total, boolean hasMore) {
    }

    public record LoginRequestDto(String account, String password, String clientType, String deviceModel) {
    }

    public record AuthSessionDto(String accessToken, String refreshToken, long expiresInSeconds, String tokenType) {
    }

    public record UserProfileDto(String userId, String name, String badgeNo, String department, String phone, String email, String dutyArea, String shiftDuration, String patrolGroup, String systemNode) {
    }

    public record DeviceStatusDto(String deviceId, String deviceName, boolean online, int batteryPercent, int signalBars, String onlineDuration, float storageUsedGb, float storageTotalGb, String firmwareVersion, String recordingStatus, boolean talking, boolean cloudConnected) {
    }

    public record ScannedDeviceDto(String deviceId, String deviceName, int signalBars, String serviceUuid, boolean bonded, String macAddress, String deviceType) {
    }

    public record DeviceCommandRequestDto(String command, String operatorId, String requestId) {
    }

    public record AlertDto(String alertId, String title, String level, String status, String occurredAt, String locationText, String source, String description, String confidence) {
    }

    public record AlertCloseRequestDto(String result, String note, String operatorId, List<UploadAttachmentDto> attachments) {
    }

    public record UploadAttachmentDto(String clientFileId, String fileName, String mimeType, Long sizeBytes, String source, String localUri, String uploadIntent) {
    }

    public record MediaFileDto(String fileId, String fileName, String mediaType, String capturedAt, String sizeText, String durationText, boolean sha256Verified, String storageSide, String transferStatus, float progress, String contentUri) {
    }

    public record TransferRequestDto(String target, int chunkSizeBytes, String resumeToken) {
    }

    public record HeartbeatRequestDto(String deviceId, boolean online, int batteryPercent, int signalBars, String recordingStatus, long clientTimestamp) {
    }

    public record HeartbeatAckDto(boolean accepted, long serverTime, int nextHeartbeatSeconds) {
    }

    public record StreamRelayRequestDto(String deviceId, String mode, String protocol) {
    }

    public record StreamRelayStateDto(String state, String relayUrl, Integer latencyMs) {
    }

    public record GpsLocationDto(double latitude, double longitude, float accuracyMeters, String address) {
    }

    public record PatrolGeoPointDto(double latitude, double longitude) {
    }

    public record PatrolAreaDto(String areaId, String areaName, String teamId, String teamName, List<PatrolGeoPointDto> boundary, List<PatrolGeoPointDto> route) {
    }

    public record SosEventDto(String sosId, String phase, String message, GpsLocationDto location, boolean recordingAudio, Integer backupEtaMinutes) {
    }
}
