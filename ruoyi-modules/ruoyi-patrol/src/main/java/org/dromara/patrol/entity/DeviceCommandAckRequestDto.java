package org.dromara.patrol.entity;

import lombok.Data;

/**
 * Android/设备对平台指令的真实执行回执。
 */
@Data
public class DeviceCommandAckRequestDto {

    private String deviceId;
    private String status;
    private String message;
}
