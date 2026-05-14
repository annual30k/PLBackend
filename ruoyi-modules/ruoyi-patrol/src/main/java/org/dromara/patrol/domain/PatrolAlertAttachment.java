package org.dromara.patrol.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 预警处置附件对象 patrol_alert_attachment
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_alert_attachment")
public class PatrolAlertAttachment extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 附件ID
     */
    @TableId(value = "attachment_id")
    private String attachmentId;

    /**
     * 告警ID
     */
    private String alertId;

    /**
     * 客户端文件ID
     */
    private String clientFileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件MIME类型
     */
    private String mimeType;

    /**
     * 文件大小（字节）
     */
    private Long sizeBytes;

    /**
     * 文件来源
     */
    private String source;

    /**
     * 本地文件地址
     */
    private String localUri;

    /**
     * 上传用途
     */
    private String uploadIntent;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
