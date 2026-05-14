package org.dromara.patrol.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 巡检媒体文件对象 patrol_media
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_media")
public class PatrolMedia extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 媒体主键
     */
    @TableId(value = "media_id")
    private Long mediaId;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 媒体类型
     */
    private String mediaType;

    /**
     * 采集时间
     */
    private String capturedAt;

    /**
     * 文件大小文本
     */
    private String sizeText;

    /**
     * 文件大小（字节）
     */
    private Long fileSizeBytes;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 时长文本
     */
    private String durationText;

    /**
     * 是否通过哈希校验
     */
    private Boolean sha256Verified;

    /**
     * 存储侧
     */
    private String storageSide;

    /**
     * 传输状态
     */
    private String transferStatus;

    /**
     * 传输进度
     */
    private Float progress;

    /**
     * 内容访问地址
     */
    private String contentUri;

    /**
     * OSS文件ID
     */
    private Long ossId;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 对象键
     */
    private String objectKey;

    /**
     * SHA256哈希值
     */
    private String sha256;

    /**
     * 证据水印令牌
     */
    private String watermarkToken;

    /**
     * 警号
     */
    private String badgeNo;

    /**
     * 警员姓名
     */
    private String officerName;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务ID
     */
    private String bizId;

    /**
     * 证据来源
     */
    private String evidenceSource;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
