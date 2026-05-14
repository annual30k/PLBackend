package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口统一响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiEnvelope<T> {

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 链路追踪ID
     */
    private String traceId;

    /**
     * 响应时间戳
     */
    private long timestamp;
}
