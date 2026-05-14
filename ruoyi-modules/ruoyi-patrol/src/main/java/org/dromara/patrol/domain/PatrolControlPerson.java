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
 * 人员布控对象 patrol_control_person
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_control_person")
public class PatrolControlPerson extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 布控ID
     */
    @TableId(value = "control_id")
    private String controlId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 布控类别
     */
    private String category;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 布控状态
     */
    private String status;

    /**
     * 数据来源
     */
    private String source;

    /**
     * 到期时间
     */
    private Date expiresAt;

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
