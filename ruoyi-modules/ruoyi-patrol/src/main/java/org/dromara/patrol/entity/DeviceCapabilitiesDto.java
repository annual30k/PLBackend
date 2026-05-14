package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备能力信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCapabilitiesDto {

    /**
     * 是否支持眼镜能力
     */
    private boolean supportsGlasses;

    /**
     * 是否支持耳机能力
     */
    private boolean supportsEarphone;

    /**
     * 是否支持设备Wi-Fi
     */
    private boolean supportsWifi;

    /**
     * 是否支持文件传输
     */
    private boolean supportsFileTransfer;

    /**
     * 是否支持拍照
     */
    private boolean supportsPhoto;

    /**
     * 是否支持视频
     */
    private boolean supportsVideo;

    /**
     * 是否支持录音
     */
    private boolean supportsAudioRecord;

    /**
     * 是否支持实时音频
     */
    private boolean supportsRealtimeAudio;
}
