package org.dromara.patrol.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.patrol.entity.AlertCloseRequestDto;
import org.dromara.patrol.entity.AlertDto;
import org.dromara.patrol.entity.ApiEnvelope;
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
import org.dromara.patrol.entity.PatrolMessageDto;
import org.dromara.patrol.entity.ScannedDeviceDto;
import org.dromara.patrol.entity.SosEventDto;
import org.dromara.patrol.entity.StreamRelayRequestDto;
import org.dromara.patrol.entity.StreamRelayStateDto;
import org.dromara.patrol.entity.TransferRequestDto;
import org.dromara.patrol.entity.UserProfileDto;
import org.dromara.patrol.entity.VersionCheckDto;
import org.dromara.patrol.service.IPatrolAppService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 巡检App接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PatrolAppApiController {

    private static final long RESPONSE_TIME = 1715832000L;

    private final IPatrolAppService patrolAppService;

    @SaIgnore
    @PostMapping("/auth/login")
    public ApiEnvelope<AuthSessionDto> login(@RequestBody LoginRequestDto request) {
        return ok(patrolAppService.login(request));
    }

    @PostMapping("/auth/refresh")
    public ApiEnvelope<AuthSessionDto> refresh(@RequestBody Map<String, String> request) {
        return ok(patrolAppService.refresh(request));
    }

    @GetMapping("/users/me")
    public ApiEnvelope<UserProfileDto> currentUser() {
        return ok(patrolAppService.currentUser());
    }

    @GetMapping("/devices/scan")
    public ApiEnvelope<List<ScannedDeviceDto>> scanDevices() {
        return ok(patrolAppService.scanDevices());
    }

    @PostMapping("/devices/{deviceId}/bind")
    public ApiEnvelope<DeviceStatusDto> bindDevice(@PathVariable String deviceId) {
        return ok(patrolAppService.bindDevice(deviceId));
    }

    @PostMapping("/devices/{deviceId}/commands")
    public ApiEnvelope<DeviceStatusDto> sendDeviceCommand(@PathVariable String deviceId, @RequestBody DeviceCommandRequestDto request) {
        return ok(patrolAppService.sendDeviceCommand(deviceId, request));
    }

    @GetMapping("/devices/{deviceId}/capabilities")
    public ApiEnvelope<DeviceCapabilitiesDto> deviceCapabilities(@PathVariable String deviceId) {
        return ok(patrolAppService.deviceCapabilities(deviceId));
    }

    @GetMapping("/devices/{deviceId}/wifi")
    public ApiEnvelope<DeviceWifiStateDto> deviceWifi(@PathVariable String deviceId) {
        return ok(patrolAppService.deviceWifi(deviceId));
    }

    @PostMapping("/devices/{deviceId}/wifi")
    public ApiEnvelope<DeviceWifiStateDto> configureWifi(@PathVariable String deviceId, @RequestBody DeviceWifiStateDto request) {
        return ok(patrolAppService.configureWifi(deviceId, request));
    }

    @PostMapping("/devices/{deviceId}/settings")
    public ApiEnvelope<DeviceAdvancedSettingsDto> applySettings(@PathVariable String deviceId, @RequestBody DeviceAdvancedSettingsDto request) {
        return ok(patrolAppService.applySettings(deviceId, request));
    }

    @PostMapping("/devices/{deviceId}/realtime-audio/start")
    public ApiEnvelope<DeviceControlResultDto> startRealtimeAudioSync(@PathVariable String deviceId) {
        return ok(patrolAppService.startRealtimeAudioSync(deviceId));
    }

    @PostMapping("/devices/{deviceId}/realtime-audio/stop")
    public ApiEnvelope<DeviceControlResultDto> stopRealtimeAudioSync(@PathVariable String deviceId) {
        return ok(patrolAppService.stopRealtimeAudioSync(deviceId));
    }

    @PostMapping("/devices/{deviceId}/media-sync/completed")
    public ApiEnvelope<DeviceControlResultDto> notifyMediaSyncCompleted(@PathVariable String deviceId) {
        return ok(patrolAppService.notifyMediaSyncCompleted(deviceId));
    }

    @GetMapping("/alerts")
    public ApiEnvelope<PageEnvelope<AlertDto>> alerts(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return ok(patrolAppService.alerts(page, pageSize));
    }

    @PostMapping("/alerts/{alertId}/ack")
    public ApiEnvelope<AlertDto> acknowledgeAlert(@PathVariable String alertId) {
        return ok(patrolAppService.acknowledgeAlert(alertId));
    }

    @PostMapping("/alerts/{alertId}/close")
    public ApiEnvelope<AlertDto> closeAlert(@PathVariable String alertId, @RequestBody AlertCloseRequestDto request) {
        return ok(patrolAppService.closeAlert(alertId, request));
    }

    @GetMapping("/media")
    public ApiEnvelope<PageEnvelope<MediaFileDto>> mediaFiles(
        @RequestParam(defaultValue = "DEVICE") String side,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "50") int pageSize) {
        return ok(patrolAppService.mediaFiles(side, page, pageSize));
    }

    @PostMapping(value = "/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiEnvelope<MediaFileDto> uploadMedia(
        @RequestPart("file") MultipartFile file,
        @RequestParam(defaultValue = "PHONE") String storageSide,
        @RequestParam(defaultValue = "") String bizType,
        @RequestParam(defaultValue = "") String bizId) {
        return ok(patrolAppService.uploadMedia(file, storageSide, bizType, bizId));
    }

    @PostMapping("/media/upload-tasks")
    public ApiEnvelope<MediaUploadTaskDto> createMediaUploadTask(@RequestBody MediaUploadTaskCreateDto request) {
        return ok(patrolAppService.createMediaUploadTask(request));
    }

    @GetMapping("/media/upload-tasks")
    public ApiEnvelope<PageEnvelope<MediaUploadTaskDto>> mediaUploadTasks(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return ok(patrolAppService.mediaUploadTasks(page, pageSize));
    }

    @GetMapping("/media/upload-tasks/{taskId}")
    public ApiEnvelope<MediaUploadTaskDto> mediaUploadTask(@PathVariable String taskId) {
        return ok(patrolAppService.mediaUploadTask(taskId));
    }

    @PostMapping(value = "/media/upload-tasks/{taskId}/chunks/{chunkIndex}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiEnvelope<MediaUploadTaskDto> uploadMediaChunk(
        @PathVariable String taskId,
        @PathVariable int chunkIndex,
        @RequestPart("chunk") MultipartFile chunk) {
        return ok(patrolAppService.uploadMediaChunk(taskId, chunkIndex, chunk));
    }

    @PostMapping("/media/upload-tasks/{taskId}/complete")
    public ApiEnvelope<MediaUploadTaskDto> completeMediaUploadTask(@PathVariable String taskId) {
        return ok(patrolAppService.completeMediaUploadTask(taskId));
    }

    @PostMapping("/media/upload-tasks/{taskId}/retry")
    public ApiEnvelope<MediaUploadTaskDto> retryMediaUploadTask(@PathVariable String taskId) {
        return ok(patrolAppService.retryMediaUploadTask(taskId));
    }

    @DeleteMapping("/media/upload-tasks/{taskId}")
    public ApiEnvelope<MediaUploadTaskDto> cancelMediaUploadTask(@PathVariable String taskId) {
        return ok(patrolAppService.cancelMediaUploadTask(taskId));
    }

    @PostMapping("/media/{fileId}/transfer")
    public List<ApiEnvelope<MediaFileDto>> transferMedia(@PathVariable String fileId, @RequestBody TransferRequestDto request) {
        return patrolAppService.transferMedia(fileId, request).stream().map(this::ok).toList();
    }

    @DeleteMapping("/media/{fileId}")
    public ApiEnvelope<Boolean> deleteMedia(@PathVariable String fileId, @RequestParam(defaultValue = "") String side) {
        return ok(patrolAppService.deleteMedia(fileId, side));
    }

    @PostMapping("/media/{fileId}/verify")
    public ApiEnvelope<Boolean> verifyMedia(@PathVariable String fileId) {
        return ok(patrolAppService.verifyMedia(fileId));
    }

    @PostMapping("/realtime/heartbeat")
    public ApiEnvelope<HeartbeatAckDto> heartbeat(@RequestBody HeartbeatRequestDto request) {
        return ok(patrolAppService.heartbeat(request));
    }

    @GetMapping("/messages")
    public ApiEnvelope<PageEnvelope<PatrolMessageDto>> messages(
        @RequestParam(defaultValue = "") String targetId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize) {
        return ok(patrolAppService.messages(targetId, page, pageSize));
    }

    @PostMapping("/messages/{messageId}/read")
    public ApiEnvelope<PatrolMessageDto> readMessage(@PathVariable String messageId) {
        return ok(patrolAppService.readMessage(messageId));
    }

    @PostMapping("/stream/start")
    public ApiEnvelope<StreamRelayStateDto> startStream(@RequestBody StreamRelayRequestDto request) {
        return ok(patrolAppService.startStream(request));
    }

    @PostMapping("/stream/stop")
    public ApiEnvelope<StreamRelayStateDto> stopStream() {
        return ok(patrolAppService.stopStream());
    }

    @PostMapping("/intercom/sessions")
    public ApiEnvelope<IntercomSessionDto> createIntercomSession(@RequestBody IntercomSessionRequestDto request) {
        return ok(patrolAppService.createIntercomSession(request));
    }

    @GetMapping("/intercom/sessions/pending")
    public ApiEnvelope<IntercomSessionDto> pendingIntercomSession(@RequestParam String deviceId) {
        return ok(patrolAppService.pendingIntercomSession(deviceId));
    }

    @PostMapping("/intercom/sessions/{sessionId}/accept")
    public ApiEnvelope<IntercomSessionDto> acceptIntercomSession(@PathVariable String sessionId) {
        return ok(patrolAppService.acceptIntercomSession(sessionId));
    }

    @PostMapping("/intercom/sessions/{sessionId}/close")
    public ApiEnvelope<IntercomSessionDto> closeIntercomSession(@PathVariable String sessionId) {
        return ok(patrolAppService.closeIntercomSession(sessionId));
    }

    @PostMapping("/intercom/sessions/{sessionId}/signals")
    public ApiEnvelope<IntercomSignalDto> sendIntercomSignal(@PathVariable String sessionId, @RequestBody IntercomSignalRequestDto request) {
        return ok(patrolAppService.sendIntercomSignal(sessionId, request));
    }

    @GetMapping("/intercom/sessions/{sessionId}/signals")
    public ApiEnvelope<List<IntercomSignalDto>> intercomSignals(@PathVariable String sessionId, @RequestParam(defaultValue = "") String afterSignalId) {
        return ok(patrolAppService.intercomSignals(sessionId, afterSignalId));
    }

    @GetMapping("/patrol/areas/current")
    public ApiEnvelope<PatrolAreaDto> currentPatrolArea() {
        return ok(patrolAppService.currentPatrolArea());
    }

    @PostMapping("/sos/activate")
    public ApiEnvelope<SosEventDto> activateSos(@RequestBody GpsLocationDto location) {
        return ok(patrolAppService.activateSos(location));
    }

    @PostMapping("/sos/cancel")
    public ApiEnvelope<SosEventDto> cancelSos() {
        return ok(patrolAppService.cancelSos());
    }

    @GetMapping("/version/check")
    public ApiEnvelope<VersionCheckDto> checkVersion(@RequestParam(defaultValue = "1") int currentVersionCode) {
        return ok(patrolAppService.checkVersion(currentVersionCode));
    }

    @SaIgnore
    @GetMapping("/cerebellum/face-library")
    public ApiEnvelope<FaceLibraryPackageDto> faceLibraryPackage(
        @RequestParam(required = false) String deviceId,
        @RequestParam(required = false) String currentVersion,
        @RequestParam(defaultValue = "false") boolean force) {
        return ok(patrolAppService.faceLibraryPackage(deviceId, currentVersion, force));
    }

    @SaIgnore
    @PostMapping("/cerebellum/face-library/ack")
    public ApiEnvelope<DeviceControlResultDto> acknowledgeFaceLibrary(@RequestBody FaceLibraryAckRequestDto request) {
        return ok(patrolAppService.acknowledgeFaceLibrary(request));
    }

    @SaIgnore
    @PostMapping("/cerebellum/face-alerts")
    public ApiEnvelope<AlertDto> reportCerebellumFaceAlert(@RequestBody CerebellumFaceAlertRequestDto request) {
        return ok(patrolAppService.reportCerebellumFaceAlert(request));
    }

    private <T> ApiEnvelope<T> ok(T data) {
        return new ApiEnvelope<>(200, "OK", data, UUID.randomUUID().toString(), RESPONSE_TIME);
    }
}
