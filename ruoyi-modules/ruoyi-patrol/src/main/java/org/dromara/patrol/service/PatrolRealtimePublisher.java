package org.dromara.patrol.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.sse.utils.SseMessageUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 巡检业务实时事件发布器。
 * <p>
 * 统一封装平台端 SSE 消息结构，避免业务服务直接依赖前端事件格式。
 */
@Slf4j
@Component
public class PatrolRealtimePublisher {

    private static final String NAMESPACE = "PATROL";

    /**
     * 广播巡检实时事件。
     *
     * @param type 事件类型
     * @param module 前端业务模块
     * @param title 通知标题
     * @param summary 通知摘要
     * @param resourceId 业务资源ID
     * @param payload 扩展业务字段
     */
    public void publish(String type, String module, String title, String summary, String resourceId, Map<String, Object> payload) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("namespace", NAMESPACE);
            event.put("type", type);
            event.put("module", module);
            event.put("title", title);
            event.put("summary", summary);
            event.put("resourceId", resourceId);
            event.put("payload", payload == null ? Map.of() : payload);
            event.put("occurredAt", Instant.now().toString());
            SseMessageUtils.publishAll(JsonUtils.toJsonString(event));
        } catch (Exception e) {
            log.warn("巡检实时事件推送失败 type={} resourceId={}", type, resourceId, e);
        }
    }

    /**
     * 构造事件载荷，自动忽略空键和空值，避免不可变 Map 不支持 null 的问题。
     *
     * @param entries key/value 交替传入
     * @return 可序列化事件载荷
     */
    public Map<String, Object> payload(Object... entries) {
        Map<String, Object> payload = new HashMap<>();
        if (entries == null) {
            return payload;
        }
        for (int i = 0; i + 1 < entries.length; i += 2) {
            Object key = entries[i];
            Object value = entries[i + 1];
            if (key != null && value != null) {
                payload.put(String.valueOf(key), value);
            }
        }
        return payload;
    }
}
