package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 告警关闭请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertCloseRequestDto {

    /**
     * 处理结果
     */
    private String result;

    /**
     * 处理备注
     */
    private String note;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 附件列表
     */
    private List<UploadAttachmentDto> attachments;
}
