package org.dromara.patrol.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.patrol.entity.AlertCloseRequestDto;
import org.dromara.patrol.entity.AlertDto;
import org.dromara.patrol.entity.ApiEnvelope;
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
import org.dromara.patrol.entity.ScannedDeviceDto;
import org.dromara.patrol.entity.SosEventDto;
import org.dromara.patrol.entity.StreamRelayRequestDto;
import org.dromara.patrol.entity.StreamRelayStateDto;
import org.dromara.patrol.entity.TransferRequestDto;
import org.dromara.patrol.entity.UserProfileDto;
import org.dromara.patrol.service.IPatrolAppService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/stream/start")
    public ApiEnvelope<StreamRelayStateDto> startStream(@RequestBody StreamRelayRequestDto request) {
        return ok(patrolAppService.startStream(request));
    }

    @PostMapping("/stream/stop")
    public ApiEnvelope<StreamRelayStateDto> stopStream() {
        return ok(patrolAppService.stopStream());
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

    private <T> ApiEnvelope<T> ok(T data) {
        return new ApiEnvelope<>(200, "OK", data, UUID.randomUUID().toString(), RESPONSE_TIME);
    }
}
