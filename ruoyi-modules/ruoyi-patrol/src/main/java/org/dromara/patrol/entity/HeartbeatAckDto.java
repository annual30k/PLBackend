package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备心跳响应信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatAckDto {

    /**
     * 是否接收心跳
     */
    private boolean accepted;

    /**
     * 服务端时间戳
     */
    private long serverTime;

    /**
     * 下次心跳间隔（秒）
     */
    private int nextHeartbeatSeconds;
}
