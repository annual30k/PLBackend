package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备控制结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceControlResultDto {

    /**
     * 是否执行成功
     */
    private boolean success;

    /**
     * 当前状态
     */
    private String state;

    /**
     * 返回消息
     */
    private String message;
}
