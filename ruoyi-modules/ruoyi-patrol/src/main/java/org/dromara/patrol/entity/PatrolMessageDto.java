package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * App消息通知数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolMessageDto {

    /**
     * 消息ID
     */
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
     * 当前接收人投递状态
     */
    private String deliveryStatus;

    /**
     * 当前接收人投递时间
     */
    private String deliveredAt;

    /**
     * 当前接收人已读时间
     */
    private String readAt;

    /**
     * 消息状态
     */
    private String status;

    /**
     * 发送时间
     */
    private String sentAt;
}
