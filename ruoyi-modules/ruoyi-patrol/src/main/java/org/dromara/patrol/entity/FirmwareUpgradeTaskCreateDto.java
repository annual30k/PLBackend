package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备固件升级任务创建请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirmwareUpgradeTaskCreateDto {

    private String firmwareId;

    private String operatorId;

    private String fromVersion;
}
