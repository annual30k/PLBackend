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
 * 设备指令记录对象 patrol_device_command
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_device_command")
public class PatrolDeviceCommand extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 指令ID
     */
    @TableId(value = "command_id")
    private String commandId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 指令编码
     */
    private String command;

    /**
     * 操作人
     */
    private String operatorId;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 指令状态
     */
    private String status;

    /**
     * 结果消息
     */
    private String resultMessage;

    /**
     * 下发时间
     */
    private Date sentAt;

    /**
     * 回执时间
     */
    private Date ackAt;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
