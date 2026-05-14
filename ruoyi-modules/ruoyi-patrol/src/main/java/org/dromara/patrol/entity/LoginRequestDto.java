package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    /**
     * 登录账号
     */
    private String account;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 客户端类型
     */
    private String clientType;

    /**
     * 设备型号
     */
    private String deviceModel;
}
