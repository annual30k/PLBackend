package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备状态信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusDto {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 是否在线
     */
    private boolean online;

    /**
     * 电量百分比
     */
    private int batteryPercent;

    /**
     * 信号格数
     */
    private int signalBars;

    /**
     * 在线时长
     */
    private String onlineDuration;

    /**
     * 已用存储容量（GB）
     */
    private float storageUsedGb;

    /**
     * 总存储容量（GB）
     */
    private float storageTotalGb;

    /**
     * 固件版本
     */
    private String firmwareVersion;

    /**
     * 录音状态
     */
    private String recordingStatus;

    /**
     * 是否对讲中
     */
    private boolean talking;

    /**
     * 是否连接云端
     */
    private boolean cloudConnected;
}
