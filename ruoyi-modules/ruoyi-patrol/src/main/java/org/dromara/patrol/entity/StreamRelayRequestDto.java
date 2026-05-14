package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实时流转发请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamRelayRequestDto {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 转发模式
     */
    private String mode;

    /**
     * 转发协议
     */
    private String protocol;
}
