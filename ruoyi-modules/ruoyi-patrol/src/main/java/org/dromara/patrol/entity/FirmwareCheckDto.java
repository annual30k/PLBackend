package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 设备固件检查结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirmwareCheckDto {

    private boolean hasUpdate;

    private String firmwareId;

    private String deviceType;

    private String vendor;

    private String chipset;

    private String deviceModel;

    private String hardwareVersion;

    private String firmwareType;

    private Integer versionCode;

    private String versionName;

    private boolean forceUpdate;

    private List<String> changelog;

    private String downloadUrl;

    private String sha256;

    private String fileId;

    private Long fileSizeBytes;

    private String packageFormat;

    private String upgradeMode;

    private String currentFirmwareVersion;

    private String message;
}
