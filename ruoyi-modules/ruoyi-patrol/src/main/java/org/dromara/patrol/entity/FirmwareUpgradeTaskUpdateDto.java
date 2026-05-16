package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备固件升级任务状态更新请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirmwareUpgradeTaskUpdateDto {

    private String status;

    private Float progress;

    private String errorCode;

    private String errorMessage;
}
