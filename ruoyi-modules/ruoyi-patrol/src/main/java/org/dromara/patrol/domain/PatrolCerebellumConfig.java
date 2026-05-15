package org.dromara.patrol.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 用户小脑连接配置对象 patrol_cerebellum_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_cerebellum_config")
public class PatrolCerebellumConfig extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(value = "config_id")
    private String configId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名/警号
     */
    private String userName;

    /**
     * 警号
     */
    private String badgeNo;

    /**
     * 小脑服务地址
     */
    private String baseUrl;

    /**
     * 小脑API Key
     */
    private String apiKey;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
