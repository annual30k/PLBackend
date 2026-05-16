package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备固件升级任务
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirmwareUpgradeTaskDto {

    private String taskId;

    private String deviceId;

    private String firmwareId;

    private String operatorId;

    private String fromVersion;

    private String toVersion;

    private String status;

    private Float progress;

    private String errorCode;

    private String errorMessage;

    private String startedAt;

    private String finishedAt;
}
