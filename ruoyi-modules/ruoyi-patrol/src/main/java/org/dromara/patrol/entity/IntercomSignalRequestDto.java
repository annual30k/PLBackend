package org.dromara.patrol.entity;

import lombok.Data;

/**
 * WebRTC 对讲信令请求
 */
@Data
public class IntercomSignalRequestDto {

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
}
