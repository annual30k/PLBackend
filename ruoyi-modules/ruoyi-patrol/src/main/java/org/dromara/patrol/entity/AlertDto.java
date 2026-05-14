package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 告警事件信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertDto {

    /**
     * 告警ID
     */
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
}
