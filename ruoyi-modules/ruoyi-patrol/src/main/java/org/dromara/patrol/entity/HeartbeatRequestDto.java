package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备心跳请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatRequestDto {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 是否在线
     */
    private boolean online;

    /**
     * 电量百分比
     */
    private int batteryPercent;

    /**
     * 信号格数
     */
    private int signalBars;

    /**
     * 录音状态
     */
    private String recordingStatus;

    /**
     * 客户端时间戳
     */
    private long clientTimestamp;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 定位精度（米）
     */
    private Float accuracyMeters;

    /**
     * 地址描述
     */
    private String address;
}
