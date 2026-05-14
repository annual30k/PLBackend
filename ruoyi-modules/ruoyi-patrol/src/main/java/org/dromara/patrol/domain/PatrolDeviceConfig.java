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
 * 巡检设备能力与高级配置对象 patrol_device_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_device_config")
public class PatrolDeviceConfig extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(value = "config_id")
    private String configId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 是否支持眼镜能力
     */
    private Boolean supportsGlasses;

    /**
     * 是否支持耳机能力
     */
    private Boolean supportsEarphone;

    /**
     * 是否支持设备Wi-Fi
     */
    private Boolean supportsWifi;

    /**
     * 是否支持文件传输
     */
    private Boolean supportsFileTransfer;

    /**
     * 是否支持拍照
     */
    private Boolean supportsPhoto;

    /**
     * 是否支持视频
     */
    private Boolean supportsVideo;

    /**
     * 是否支持录音
     */
    private Boolean supportsAudioRecord;

    /**
     * 是否支持实时音频
     */
    private Boolean supportsRealtimeAudio;

    /**
     * Wi-Fi是否启用
     */
    private Boolean wifiEnabled;

    /**
     * Wi-Fi SSID
     */
    private String wifiSsid;

    /**
     * 是否已配置Wi-Fi密码
     */
    private Boolean wifiPasswordConfigured;

    /**
     * Wi-Fi是否已连接
     */
    private Boolean wifiConnected;

    /**
     * 视频宽度
     */
    private Integer videoWidth;

    /**
     * 视频高度
     */
    private Integer videoHeight;

    /**
     * 视频帧率
     */
    private Integer videoFrameRate;

    /**
     * 录制时长（秒）
     */
    private Integer recordingDurationSeconds;

    /**
     * 是否竖屏录制
     */
    private Boolean verticalRecording;

    /**
     * 是否开启增强音效
     */
    private Boolean enhancedSound;

    /**
     * 亮度档位
     */
    private Integer brightnessLevel;

    /**
     * 是否处于实时音频同步
     */
    private Boolean realtimeAudioSyncing;

    /**
     * 最近媒体同步完成时间
     */
    private Date lastMediaSyncAt;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
