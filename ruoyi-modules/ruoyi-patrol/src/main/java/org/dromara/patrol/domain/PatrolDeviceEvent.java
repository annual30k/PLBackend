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
 * 设备事件对象 patrol_device_event
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_device_event")
public class PatrolDeviceEvent extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件ID
     */
    @TableId(value = "event_id")
    private String eventId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件级别
     */
    private String eventLevel;

    /**
     * 事件标题
     */
    private String eventTitle;

    /**
     * 事件详情
     */
    private String eventDetail;

    /**
     * 发生时间
     */
    private Date occurredAt;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
