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
 * 边缘小脑日报对象 patrol_daily_report
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patrol_daily_report")
public class PatrolDailyReport extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日报ID
     */
    @TableId(value = "report_id")
    private String reportId;

    /**
     * 任务ID
     */
    private String missionId;

    /**
     * 报告类型
     */
    private String reportType;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 警号
     */
    private String operatorId;

    /**
     * 警员姓名
     */
    private String officerName;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 生成后端
     */
    private String backend;

    /**
     * 生成时间
     */
    private Date generatedAt;

    /**
     * 报告正文
     */
    private String content;

    /**
     * Word报告文件地址
     */
    private String documentUri;

    /**
     * Word报告文件名
     */
    private String documentName;

    /**
     * 报告文件格式
     */
    private String documentFormat;

    /**
     * 媒体选择JSON
     */
    private String mediaSelectionJson;

    /**
     * 结构化上下文JSON
     */
    private String structuredContextJson;

    /**
     * 是否需要人工复核
     */
    private Boolean requiresHumanConfirmation;

    /**
     * 状态
     */
    private String status;

    /**
     * 提交来源
     */
    private String submitSource;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
