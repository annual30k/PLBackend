package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 边缘小脑人脸库版本包。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceLibraryPackageDto {

    private String version;

    private String source;

    private boolean fullSnapshot;

    private String model;

    private String deviceId;

    private boolean unchanged;

    private long generatedAt;

    private List<FaceLibraryPersonDto> persons;
}
