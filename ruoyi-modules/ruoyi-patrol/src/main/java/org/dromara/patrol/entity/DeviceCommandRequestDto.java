package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备指令请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCommandRequestDto {

    /**
     * 指令编码
     */
    private String command;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 请求ID
     */
    private String requestId;
}
