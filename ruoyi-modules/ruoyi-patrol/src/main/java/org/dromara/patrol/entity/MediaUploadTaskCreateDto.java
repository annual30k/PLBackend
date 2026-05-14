package org.dromara.patrol.entity;

import lombok.Data;

/**
 * 媒体分片上传任务创建请求
 */
@Data
public class MediaUploadTaskCreateDto {

    private String fileName;
    private String mediaType;
    private String mimeType;
    private long fileSizeBytes;
    private long chunkSizeBytes;
    private int totalChunks;
    private String sha256;
    private String storageSide;
    private String bizType;
    private String bizId;
}
