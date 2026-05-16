package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备固件检查请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirmwareCheckRequestDto {

    private String deviceType;

    private String vendor;

    private String chipset;

    private String deviceModel;

    private String hardwareVersion;

    private String currentFirmwareVersion;
}
