package org.dromara.patrol.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 边缘小脑多帧人脸候选告警上报。
 */
@Data
public class CerebellumFaceAlertRequestDto {

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

    @JsonProperty("person_id")
    private String personId;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("risk_level")
    private String riskLevel;

    private String category;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty("confirm_frames")
    private Integer confirmFrames;

    @JsonProperty("window_seconds")
    private Double windowSeconds;

    @JsonProperty("average_similarity")
    private Double averageSimilarity;

    @JsonProperty("occurred_at")
    private String occurredAt;
}
