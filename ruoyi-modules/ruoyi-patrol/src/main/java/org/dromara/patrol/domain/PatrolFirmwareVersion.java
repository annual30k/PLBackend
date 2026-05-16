package org.dromara.patrol.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.util.Date;

/**
 * 设备固件版本对象 patrol_firmware_version
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_firmware_version")
public class PatrolFirmwareVersion extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "firmware_id")
    private String firmwareId;

    private String deviceType;

    private String vendor;

    private String chipset;

    private String deviceModel;

    private String hardwareVersion;

    private String firmwareType;

    private Integer versionCode;

    private String versionName;

    private String minCurrentVersion;

    private String maxCurrentVersion;

    private Boolean forceUpdate;

    private String changelog;

    private String downloadUrl;

    private String sha256;

    private String fileId;

    private Long fileSizeBytes;

    private String packageFormat;

    private String upgradeMode;

    private String grayScope;

    private String grayTargets;

    private String status;

    private Date publishedAt;

    private String remark;

    @TableLogic
    private String delFlag;
}
