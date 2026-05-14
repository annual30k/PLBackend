package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * WebRTC/VoIP 对讲会话
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntercomSessionDto {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 目标设备ID
     */
    private String deviceId;

    /**
     * 会话状态：WAITING_APP / SIGNALING / ACTIVE / CLOSED
     */
    private String state;

    /**
     * 会话模式：PTT / FULL_DUPLEX
     */
    private String mode;

    /**
     * 信令交换地址
     */
    private String signalingUrl;

    /**
     * 推荐音频路由
     */
    private String audioRoute;

    /**
     * ICE 服务配置
     */
    private List<String> iceServers;

    /**
     * 会话创建时间戳
     */
    private Long startedAt;

    /**
     * 会话过期时间戳
     */
    private Long expiresAt;

    /**
     * 状态说明
     */
    private String message;
}
