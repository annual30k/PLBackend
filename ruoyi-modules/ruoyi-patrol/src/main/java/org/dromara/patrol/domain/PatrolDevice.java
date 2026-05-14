package org.dromara.patrol.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.util.Date;

/**
 * 巡检设备对象 patrol_device
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_device")
public class PatrolDevice extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @TableId(value = "device_id")
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 蓝牙服务UUID
     */
    private String serviceUuid;

    /**
     * MAC地址
     */
    private String macAddress;

    /**
     * 是否已绑定
     */
    private Boolean bonded;

    /**
     * 是否在线
     */
    private Boolean online;

    /**
     * 电量百分比
     */
    private Integer batteryPercent;

    /**
     * 信号格数
     */
    private Integer signalBars;

    /**
     * 在线时长
     */
    private String onlineDuration;

    /**
     * 已用存储容量（GB）
     */
    private Float storageUsedGb;

    /**
     * 总存储容量（GB）
     */
    private Float storageTotalGb;

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
    private Boolean talking;

    /**
     * 是否连接云端
     */
    private Boolean cloudConnected;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 地址描述
     */
    private String address;

    /**
     * 最后心跳时间
     */
    private Date lastHeartbeatTime;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
