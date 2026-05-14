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
 * 指挥后台审计日志对象 patrol_audit_log
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_audit_log")
public class PatrolAuditLog extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "log_id")
    private String logId;

    /**
     * 日志类型
     */
    private String logType;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作动作
     */
    private String action;

    /**
     * 业务资源
     */
    private String resource;

    /**
     * 操作结果
     */
    private String result;

    /**
     * 操作IP
     */
    private String ipAddress;

    /**
     * 链路ID
     */
    private String traceId;

    /**
     * 操作时间
     */
    private Date occurredAt;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
