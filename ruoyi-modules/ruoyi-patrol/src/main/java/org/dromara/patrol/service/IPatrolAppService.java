package org.dromara.patrol.service;

import org.dromara.patrol.entity.AlertCloseRequestDto;
import org.dromara.patrol.entity.AlertDto;
import org.dromara.patrol.entity.AuthSessionDto;
import org.dromara.patrol.entity.CerebellumFaceAlertRequestDto;
import org.dromara.patrol.entity.CerebellumSettingsDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 巡检App服务接口
 */
public interface IPatrolAppService {

    AuthSessionDto login(LoginRequestDto request);

    AuthSessionDto refresh(Map<String, String> request);

    UserProfileDto currentUser();

    CerebellumSettingsDto cerebellumSettings();

    CerebellumSettingsDto saveCerebellumSettings(CerebellumSettingsDto request);

    List<ScannedDeviceDto> scanDevices();

    DeviceStatusDto bindDevice(String deviceId);

    DeviceStatusDto unbindDevice(String deviceId);

    DeviceStatusDto sendDeviceCommand(String deviceId, DeviceCommandRequestDto request);

    DeviceCapabilitiesDto deviceCapabilities(String deviceId);

    DeviceWifiStateDto deviceWifi(String deviceId);

    DeviceWifiStateDto configureWifi(String deviceId, DeviceWifiStateDto request);

    DeviceAdvancedSettingsDto applySettings(String deviceId, DeviceAdvancedSettingsDto request);

    DeviceControlResultDto startRealtimeAudioSync(String deviceId);

    DeviceControlResultDto stopRealtimeAudioSync(String deviceId);

    DeviceControlResultDto notifyMediaSyncCompleted(String deviceId);

    PageEnvelope<AlertDto> alerts(int page, int pageSize);

    AlertDto acknowledgeAlert(String alertId);

    AlertDto closeAlert(String alertId, AlertCloseRequestDto request);

    PageEnvelope<MediaFileDto> mediaFiles(String side, int page, int pageSize);

    MediaFileDto uploadMedia(MultipartFile file, String storageSide, String bizType, String bizId);

    MediaUploadTaskDto createMediaUploadTask(MediaUploadTaskCreateDto request);

    MediaUploadTaskDto uploadMediaChunk(String taskId, int chunkIndex, MultipartFile chunk);

    MediaUploadTaskDto completeMediaUploadTask(String taskId);

    MediaUploadTaskDto mediaUploadTask(String taskId);

    PageEnvelope<MediaUploadTaskDto> mediaUploadTasks(int page, int pageSize);

    MediaUploadTaskDto retryMediaUploadTask(String taskId);

    MediaUploadTaskDto cancelMediaUploadTask(String taskId);

    Integer cleanExpiredMediaUploadTasks(int retentionHours);

    List<MediaFileDto> transferMedia(String fileId, TransferRequestDto request);

    Boolean deleteMedia(String fileId, String side);

    Boolean verifyMedia(String fileId);

    HeartbeatAckDto heartbeat(HeartbeatRequestDto request);

    PageEnvelope<PatrolMessageDto> messages(String targetId, int page, int pageSize);

    PatrolMessageDto readMessage(String messageId);

    StreamRelayStateDto startStream(StreamRelayRequestDto request);

    StreamRelayStateDto stopStream();

    IntercomSessionDto createIntercomSession(IntercomSessionRequestDto request);

    IntercomSessionDto pendingIntercomSession(String deviceId);

    IntercomSessionDto acceptIntercomSession(String sessionId);

    IntercomSessionDto closeIntercomSession(String sessionId);

    IntercomSignalDto sendIntercomSignal(String sessionId, IntercomSignalRequestDto request);

    List<IntercomSignalDto> intercomSignals(String sessionId, String afterSignalId);

    PatrolAreaDto currentPatrolArea();

    SosEventDto activateSos(GpsLocationDto location);

    SosEventDto cancelSos();

    VersionCheckDto checkVersion(int currentVersionCode);

    FaceLibraryPackageDto faceLibraryPackage(String deviceId, String currentVersion, boolean force);

    DeviceControlResultDto acknowledgeFaceLibrary(FaceLibraryAckRequestDto request);

    AlertDto reportCerebellumFaceAlert(CerebellumFaceAlertRequestDto request);
}
