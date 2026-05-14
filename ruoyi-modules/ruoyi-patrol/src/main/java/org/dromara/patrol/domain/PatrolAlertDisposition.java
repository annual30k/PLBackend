package org.dromara.patrol.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.util.Date;

/**
 * 预警处置流水对象 patrol_alert_disposition
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_alert_disposition")
public class PatrolAlertDisposition extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 处置流水ID
     */
    @TableId(value = "disposition_id")
    private String dispositionId;

    /**
     * 预警ID
     */
    private String alertId;

    /**
     * 动作类型
     */
    private String actionType;

    /**
     * 动作结果
     */
    private String actionResult;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 处置说明
     */
    private String note;

    /**
     * 附件数量
     */
    private Integer attachmentsCount;

    /**
     * 发生时间
     */
    private Date occurredAt;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
