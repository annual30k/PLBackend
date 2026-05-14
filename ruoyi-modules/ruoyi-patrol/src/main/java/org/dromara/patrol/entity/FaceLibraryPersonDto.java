package org.dromara.patrol.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 下发给边缘小脑的人脸库人员记录。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceLibraryPersonDto {

    @JsonProperty("person_id")
    private String personId;

    @JsonProperty("control_id")
    private String controlId;

    @JsonProperty("display_name")
    private String displayName;

    private String name;

    private String category;

    @JsonProperty("risk_level")
    private String riskLevel;

    private String status;

    private String source;

    @JsonProperty("expires_at")
    private Date expiresAt;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("image_sha256")
    private String imageSha256;

    @JsonProperty("updated_at")
    private Date updatedAt;
}
