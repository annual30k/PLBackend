package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备Wi-Fi状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceWifiStateDto {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * SSID
     */
    private String ssid;

    /**
     * 是否已配置密码
     */
    private boolean passwordConfigured;

    /**
     * 是否已连接
     */
    private boolean connected;
}
