package org.dromara.patrol.entity;

import lombok.Data;

/**
 * WebRTC 对讲会话创建请求
 */
@Data
public class IntercomSessionRequestDto {

    /**
     * 目标设备ID
     */
    private String deviceId;

    /**
     * 会话模式：PTT / FULL_DUPLEX
     */
    private String mode;

    /**
     * 发起人标识
     */
    private String initiatorId;
}
