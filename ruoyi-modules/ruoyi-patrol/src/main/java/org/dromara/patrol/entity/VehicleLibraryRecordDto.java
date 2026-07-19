package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 下发给云端识别服务的车辆布控记录。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLibraryRecordDto {

    private String controlId;
    private String plateNo;
    private String vehicleDesc;
    private String vehicleType;
    private String riskLevel;
    private String status;
    private String source;
    private Date expiresAt;
}
