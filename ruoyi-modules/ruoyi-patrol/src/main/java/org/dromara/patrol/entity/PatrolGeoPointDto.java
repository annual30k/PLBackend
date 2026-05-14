package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 巡区地理坐标点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolGeoPointDto {

    /**
     * 纬度
     */
    private double latitude;

    /**
     * 经度
     */
    private double longitude;
}
