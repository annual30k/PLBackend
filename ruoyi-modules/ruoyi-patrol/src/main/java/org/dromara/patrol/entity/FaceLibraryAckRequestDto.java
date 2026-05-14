package org.dromara.patrol.entity;

import lombok.Data;

/**
 * 边缘小脑人脸库同步确认。
 */
@Data
public class FaceLibraryAckRequestDto {

    private String deviceId;

    private String version;

    private Integer received;

    private Integer applied;

    private Integer skipped;

    private Integer failed;

    private Integer pending;

    private Integer personCount;
}
