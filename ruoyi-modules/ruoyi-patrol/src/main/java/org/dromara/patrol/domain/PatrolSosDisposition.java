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
 * SOS处置流水对象 patrol_sos_disposition
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_sos_disposition")
public class PatrolSosDisposition extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流水ID
     */
    @TableId(value = "disposition_id")
    private String dispositionId;

    /**
     * SOS事件ID
     */
    private String sosId;

    /**
     * 处置动作类型
     */
    private String actionType;

    /**
     * 处置结果
     */
    private String actionResult;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 处置说明
     */
    private String note;

    /**
     * 通知联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 附件文件ID
     */
    private String attachmentFileId;

    /**
     * 附件文件名
     */
    private String attachmentFileName;

    /**
     * 增援预计到达时间（分钟）
     */
    private Integer backupEtaMinutes;

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
