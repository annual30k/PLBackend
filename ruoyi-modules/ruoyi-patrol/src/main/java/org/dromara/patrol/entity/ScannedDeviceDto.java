package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 扫描到的设备信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScannedDeviceDto {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 信号格数
     */
    private int signalBars;

    /**
     * 蓝牙服务UUID
     */
    private String serviceUuid;

    /**
     * 是否已绑定
     */
    private boolean bonded;

    /**
     * 设备MAC地址
     */
    private String macAddress;

    /**
     * 设备类型
     */
    private String deviceType;
}
