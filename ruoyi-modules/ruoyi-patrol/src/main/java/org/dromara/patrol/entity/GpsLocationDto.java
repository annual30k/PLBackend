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
}
