package org.dromara.patrol.entity;

import lombok.Data;

/**
 * 边缘小脑日报提交请求。
 */
@Data
public class CerebellumDailyReportRequestDto {

    private String reportId;
    private String missionId;
    private String reportType;
    private String deviceId;
    private String operatorId;
    private String officerName;
    private String model;
    private String backend;
    private String generatedAt;
    private String content;
    private String documentUri;
    private String documentName;
    private String documentFormat;
    private Boolean requiresHumanConfirmation;
    private Object mediaSelection;
    private Object structuredContext;
}
