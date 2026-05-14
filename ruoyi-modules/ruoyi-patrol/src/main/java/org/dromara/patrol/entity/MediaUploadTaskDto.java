package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 媒体分片上传任务状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadTaskDto {

    private String taskId;
    private String fileId;
    private String fileName;
    private String mediaType;
    private String mimeType;
    private long fileSizeBytes;
    private long chunkSizeBytes;
    private int totalChunks;
    private int uploadedChunks;
    private List<Integer> uploadedChunkIndexes;
    private long uploadedBytes;
    private String expectedSha256;
    private String actualSha256;
    private String storageSide;
    private String bizType;
    private String bizId;
    private String status;
    private float progress;
    private String errorMessage;
    private String badgeNo;
    private String officerName;
    private String deviceId;
    private String completedAt;
}
