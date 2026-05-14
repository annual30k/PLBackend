package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 当前巡区信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolAreaDto {

    /**
     * 巡区ID
     */
    private String areaId;

    /**
     * 巡区名称
     */
    private String areaName;

    /**
     * 队伍ID
     */
    private String teamId;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 巡区边界
     */
    private List<PatrolGeoPointDto> boundary;

    /**
     * 巡逻路线
     */
    private List<PatrolGeoPointDto> route;
}
