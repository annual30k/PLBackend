package org.dromara.patrol.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 巡检紧急求助事件对象 patrol_sos_event
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_sos_event")
public class PatrolSosEvent extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 求助事件ID
     */
    @TableId(value = "sos_id")
    private String sosId;

    /**
     * 事件阶段
     */
    private String phase;

    /**
     * 事件消息
     */
    private String message;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 定位精度（米）
     */
    private Float accuracyMeters;

    /**
     * 地址描述
     */
    private String address;

    /**
     * 是否正在录音
     */
    private Boolean recordingAudio;

    /**
     * 增援预计到达时间（分钟）
     */
    private Integer backupEtaMinutes;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
