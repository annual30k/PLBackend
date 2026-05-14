package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备高级设置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAdvancedSettingsDto {

    /**
     * 视频宽度
     */
    private int videoWidth;

    /**
     * 视频高度
     */
    private int videoHeight;

    /**
     * 视频帧率
     */
    private int videoFrameRate;

    /**
     * 录制时长（秒）
     */
    private int recordingDurationSeconds;

    /**
     * 是否竖屏录制
     */
    private boolean verticalRecording;

    /**
     * 是否开启增强音效
     */
    private boolean enhancedSound;

    /**
     * 亮度档位
     */
    private int brightnessLevel;
}
