package org.dromara.patrol.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 巡检告警对象 patrol_alert
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_alert")
public class PatrolAlert extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 告警ID
     */
    @TableId(value = "alert_id")
    private String alertId;

    /**
     * 告警标题
     */
    private String title;

    /**
     * 告警级别
     */
    private String level;

    /**
     * 处理状态
     */
    private String status;

    /**
     * 发生时间
     */
    private String occurredAt;

    /**
     * 位置描述
     */
    private String locationText;

    /**
     * 告警来源
     */
    private String source;

    /**
     * 告警描述
     */
    private String description;

    /**
     * 置信度
     */
    private String confidence;

    /**
     * 关闭结果
     */
    private String closeResult;

    /**
     * 关闭备注
     */
    private String closeNote;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
