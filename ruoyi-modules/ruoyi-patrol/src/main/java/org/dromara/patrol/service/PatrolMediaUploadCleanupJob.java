package org.dromara.patrol.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 巡检媒体上传临时分片清理任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PatrolMediaUploadCleanupJob {

    private final IPatrolAppService patrolAppService;

    /**
     * 每小时清理超过24小时仍未完成的分片上传任务。
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanExpiredUploadTasks() {
        try {
            Integer cleaned = patrolAppService.cleanExpiredMediaUploadTasks(24);
            if (cleaned > 0) {
                log.info("巡检媒体过期分片上传任务清理完成，数量={}", cleaned);
            }
        } catch (Exception e) {
            log.warn("巡检媒体过期分片上传任务清理失败", e);
        }
    }
}
