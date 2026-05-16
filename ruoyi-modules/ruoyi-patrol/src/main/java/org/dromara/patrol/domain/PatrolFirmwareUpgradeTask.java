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
 * 设备固件升级任务对象 patrol_firmware_upgrade_task
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_firmware_upgrade_task")
public class PatrolFirmwareUpgradeTask extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "task_id")
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

    private Date startedAt;

    private Date finishedAt;

    @TableLogic
    private String delFlag;
}
