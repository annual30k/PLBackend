package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 媒体传输请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    /**
     * 传输目标
     */
    private String target;

    /**
     * 分片大小（字节）
     */
    private int chunkSizeBytes;

    /**
     * 断点续传令牌
     */
    private String resumeToken;
}
