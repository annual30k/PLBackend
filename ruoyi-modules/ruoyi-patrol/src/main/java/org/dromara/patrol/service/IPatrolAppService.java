package org.dromara.patrol.service;

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
import org.dromara.patrol.entity.ScannedDeviceDto;
import org.dromara.patrol.entity.SosEventDto;
import org.dromara.patrol.entity.StreamRelayRequestDto;
import org.dromara.patrol.entity.StreamRelayStateDto;
import org.dromara.patrol.entity.TransferRequestDto;
import org.dromara.patrol.entity.UserProfileDto;

import java.util.List;
import java.util.Map;

/**
 * 巡检App服务接口
 */
public interface IPatrolAppService {

    AuthSessionDto login(LoginRequestDto request);

    AuthSessionDto refresh(Map<String, String> request);

    UserProfileDto currentUser();

    List<ScannedDeviceDto> scanDevices();

    DeviceStatusDto bindDevice(String deviceId);

    DeviceStatusDto sendDeviceCommand(String deviceId, DeviceCommandRequestDto request);

    PageEnvelope<AlertDto> alerts(int page, int pageSize);

    AlertDto acknowledgeAlert(String alertId);

    AlertDto closeAlert(String alertId, AlertCloseRequestDto request);

    PageEnvelope<MediaFileDto> mediaFiles(String side, int page, int pageSize);

    List<MediaFileDto> transferMedia(String fileId, TransferRequestDto request);

    Boolean deleteMedia(String fileId, String side);

    Boolean verifyMedia(String fileId);

    HeartbeatAckDto heartbeat(HeartbeatRequestDto request);

    StreamRelayStateDto startStream(StreamRelayRequestDto request);

    StreamRelayStateDto stopStream();

    PatrolAreaDto currentPatrolArea();

    SosEventDto activateSos(GpsLocationDto location);

    SosEventDto cancelSos();
}
