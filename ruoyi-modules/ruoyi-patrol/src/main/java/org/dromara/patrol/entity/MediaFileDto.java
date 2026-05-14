package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 媒体文件信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileDto {

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
     * 时长文本
     */
    private String durationText;

    /**
     * 是否通过哈希校验
     */
    private boolean sha256Verified;

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
    private float progress;

    /**
     * 内容访问地址
     */
    private String contentUri;
}
