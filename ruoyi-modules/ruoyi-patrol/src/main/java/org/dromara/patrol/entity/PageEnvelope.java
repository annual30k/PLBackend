package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageEnvelope<T> {

    /**
     * 当前页数据
     */
    private List<T> items;

    /**
     * 当前页码
     */
    private int page;

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 是否还有下一页
     */
    private boolean hasMore;
}
