package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 云端车牌识别服务使用的车辆布控快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLibraryPackageDto {

    private String version;
    private String source;
    private boolean fullSnapshot;
    private String deviceId;
    private boolean unchanged;
    private long generatedAt;
    private List<VehicleLibraryRecordDto> vehicles;
}
