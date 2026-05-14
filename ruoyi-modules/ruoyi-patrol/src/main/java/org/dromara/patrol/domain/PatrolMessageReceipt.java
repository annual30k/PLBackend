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
 * 指挥消息接收明细对象 patrol_message_receipt
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_message_receipt")
public class PatrolMessageReceipt extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 接收明细ID
     */
    @TableId(value = "receipt_id")
    private String receiptId;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 接收人警号或目标标识
     */
    private String recipientId;

    /**
     * 接收人名称
     */
    private String recipientName;

    /**
     * 绑定设备ID
     */
    private String deviceId;

    /**
     * 投递状态
     */
    private String deliveryStatus;

    /**
     * 投递时间
     */
    private Date deliveredAt;

    /**
     * 已读时间
     */
    private Date readAt;

    /**
     * 最近拉取时间
     */
    private Date lastPullAt;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
