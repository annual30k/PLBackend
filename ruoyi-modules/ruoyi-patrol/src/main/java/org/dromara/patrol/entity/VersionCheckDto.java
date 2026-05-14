package org.dromara.patrol.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * App版本检查结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionCheckDto {

    /**
     * 最新版本号编码
     */
    private int latestVersionCode;

    /**
     * 最新版本名称
     */
    private String latestVersionName;

    /**
     * 是否强制更新
     */
    private boolean forceUpdate;

    /**
     * 更新日志
     */
    private List<String> changelog;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 安装包SHA-256
     */
    private String sha256;
}
