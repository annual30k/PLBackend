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
 * 警力位置轨迹对象 patrol_location_track
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_location_track")
public class PatrolLocationTrack extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 轨迹ID
     */
    @TableId(value = "track_id")
    private String trackId;

    /**
     * 警号
     */
    private String badgeNo;

    /**
     * 警员姓名
     */
    private String officerName;

    /**
     * 设备ID
     */
    private String deviceId;

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
     * 上报时间
     */
    private Date reportedAt;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
