package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 平台等待 Android/设备执行的指令。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingDeviceCommandDto {

    private String commandId;
    private String deviceId;
    private String command;
    private String requestId;
    private String operatorId;
    private long sentAt;
}
