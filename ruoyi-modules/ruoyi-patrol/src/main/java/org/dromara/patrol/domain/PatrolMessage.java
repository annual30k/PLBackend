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
 * 指挥消息对象 patrol_message
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_message")
public class PatrolMessage extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(value = "message_id")
    private String messageId;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 目标ID
     */
    private String targetId;

    /**
     * 目标名称
     */
    private String targetName;

    /**
     * 发送通道
     */
    private String channel;

    /**
     * 发送状态
     */
    private String status;

    /**
     * 已读数量
     */
    private Integer readCount;

    /**
     * 目标总数
     */
    private Integer totalCount;

    /**
     * 发送时间
     */
    private Date sentAt;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
