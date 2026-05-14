package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 紧急求助事件信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SosEventDto {

    /**
     * 求助事件ID
     */
    private String sosId;

    /**
     * 事件阶段
     */
    private String phase;

    /**
     * 事件消息
     */
    private String message;

    /**
     * 事件位置
     */
    private GpsLocationDto location;

    /**
     * 是否正在录音
     */
    private boolean recordingAudio;

    /**
     * 增援预计到达时间（分钟）
     */
    private Integer backupEtaMinutes;
}
