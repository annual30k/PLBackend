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
 * 巡检App版本对象 patrol_app_version
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_app_version")
public class PatrolAppVersion extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 版本ID
     */
    @TableId(value = "version_id")
    private String versionId;

    /**
     * 版本号编码
     */
    private Integer versionCode;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 是否强制更新
     */
    private Boolean forceUpdate;

    /**
     * 更新日志，按行存储
     */
    private String changelog;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 安装包SHA-256
     */
    private String sha256;

    /**
     * 关联文件ID
     */
    private String fileId;

    /**
     * 状态：DRAFT/PUBLISHED/DISABLED
     */
    private String status;

    /**
     * 发布时间
     */
    private Date publishedAt;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
