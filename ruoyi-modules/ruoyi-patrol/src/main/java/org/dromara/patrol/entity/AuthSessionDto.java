package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录会话信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthSessionDto {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌有效期（秒）
     */
    private long expiresInSeconds;

    /**
     * 令牌类型
     */
    private String tokenType;
}
