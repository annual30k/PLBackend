package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传附件信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadAttachmentDto {

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
}
