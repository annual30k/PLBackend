package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实时流转发状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamRelayStateDto {

    /**
     * 转发状态
     */
    private String state;

    /**
     * 转发地址
     */
    private String relayUrl;

    /**
     * 延迟毫秒数
     */
    private Integer latencyMs;
}
