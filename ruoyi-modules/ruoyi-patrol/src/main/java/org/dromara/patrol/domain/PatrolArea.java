package org.dromara.patrol.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 巡检区域对象 patrol_area
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_area")
public class PatrolArea extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 区域ID
     */
    @TableId(value = "area_id")
    private String areaId;

    /**
     * 区域名称
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
     * 区域边界JSON
     */
    private String boundaryJson;

    /**
     * 巡逻路线JSON
     */
    private String routeJson;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
