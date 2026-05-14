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
 * 设备警员绑定对象 patrol_device_binding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_device_binding")
public class PatrolDeviceBinding extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 绑定ID
     */
    @TableId(value = "binding_id")
    private String bindingId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 警员用户ID
     */
    private Long userId;

    /**
     * 登录账号/警号
     */
    private String userName;

    /**
     * 警员姓名
     */
    private String nickName;

    /**
     * 所属部门ID
     */
    private Long deptId;

    /**
     * 所属部门名称
     */
    private String deptName;

    /**
     * 警号
     */
    private String badgeNo;

    /**
     * 绑定状态
     */
    private String bindStatus;

    /**
     * 绑定时间
     */
    private Date boundAt;

    /**
     * 解绑时间
     */
    private Date unboundAt;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
