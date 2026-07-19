package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GPS定位信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GpsLocationDto {

    /**
     * 纬度
     */
    private double latitude;

    /**
     * 经度
     */
    private double longitude;

    /**
     * 定位精度（米）
     */
    private float accuracyMeters;

    /**
     * 地址描述
     */
    private String address;

    /**
     * 产生该位置的当前执法设备ID（SOS等业务用于建立跨端关联）。
     */
    private String deviceId;

    /**
     * App 生成的 SOS 幂等事件ID。弱网重试时平台沿用该ID，确保录音、取消和处置时间线指向同一事件。
     */
    private String clientEventId;
}
