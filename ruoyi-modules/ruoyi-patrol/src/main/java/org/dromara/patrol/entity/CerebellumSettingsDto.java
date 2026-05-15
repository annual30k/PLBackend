package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户级小脑连接配置。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CerebellumSettingsDto {

    /**
     * 小脑服务地址
     */
    private String baseUrl;

    /**
     * 小脑API Key
     */
    private String apiKey;
}
