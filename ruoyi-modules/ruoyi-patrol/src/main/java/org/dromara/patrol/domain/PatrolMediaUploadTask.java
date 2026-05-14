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
 * 巡检媒体分片上传任务对象 patrol_media_upload_task
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_media_upload_task")
public class PatrolMediaUploadTask extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 上传任务ID
     */
    @TableId(value = "task_id")
    private String taskId;

    /**
     * 合并后媒体文件ID
     */
    private String fileId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 媒体类型
     */
    private String mediaType;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 文件总大小（字节）
     */
    private Long fileSizeBytes;

    /**
     * 分片大小（字节）
     */
    private Long chunkSizeBytes;

    /**
     * 分片总数
     */
    private Integer totalChunks;

    /**
     * 已上传分片数
     */
    private Integer uploadedChunks;

    /**
     * 已上传字节数
     */
    private Long uploadedBytes;

    /**
     * 端侧声明的SHA-256
     */
    private String expectedSha256;

    /**
     * 服务端实际SHA-256
     */
    private String actualSha256;

    /**
     * 存储侧
     */
    private String storageSide;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务ID
     */
    private String bizId;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 上传进度
     */
    private Float progress;

    /**
     * 临时分片目录
     */
    private String tempDir;

    /**
     * 失败原因
     */
    private String errorMessage;

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
     * 完成时间
     */
    private Date completedAt;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
