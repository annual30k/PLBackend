package org.dromara.patrol.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 云端车牌识别命中车辆布控后的告警上报。
 */
@Data
public class CerebellumPlateAlertRequestDto {

    @JsonProperty("alert_id")
    private String alertId;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("stream_id")
    private String streamId;

    @JsonProperty("camera_id")
    private String cameraId;

    @JsonProperty("frame_id")
    private String frameId;

    @JsonProperty("control_id")
    private String controlId;

    @JsonProperty("plate_number")
    private String plateNumber;

    @JsonProperty("vehicle_desc")
    private String vehicleDesc;

    @JsonProperty("vehicle_type")
    private String vehicleType;

    @JsonProperty("risk_level")
    private String riskLevel;

    private Double confidence;

    private String backend;

    @JsonProperty("occurred_at")
    private String occurredAt;
}
