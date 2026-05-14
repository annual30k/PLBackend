package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebRTC 对讲信令消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntercomSignalDto {

    /**
     * 信令ID
     */
    private String signalId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 发送方：WEB / APP
     */
    private String sender;

    /**
     * 信令类型：offer / answer / ice / ready / hangup
     */
    private String type;

    /**
     * SDP 或 ICE Candidate JSON
     */
    private String payload;

    /**
     * 发送时间戳
     */
    private Long timestamp;
}
