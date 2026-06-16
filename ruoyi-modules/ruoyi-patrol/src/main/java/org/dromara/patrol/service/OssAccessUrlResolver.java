package org.dromara.patrol.service;

import org.dromara.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Resolves storage URLs into URLs reachable by App clients.
 */
@Component
public class OssAccessUrlResolver {

    private final String publicBaseUrl;

    public OssAccessUrlResolver(@Value("${patrol.file.public-base-url:}") String publicBaseUrl) {
        this.publicBaseUrl = normalizeBaseUrl(publicBaseUrl);
    }

    public String toExternalUrl(String storageUrl) {
        if (StringUtils.isBlank(storageUrl) || StringUtils.isBlank(publicBaseUrl)) {
            return blankToEmpty(storageUrl);
        }
        try {
            URI source = URI.create(storageUrl);
            if (isPresignedUrl(source)) {
                return storageUrl;
            }
            URI base = URI.create(publicBaseUrl);
            StringBuilder builder = new StringBuilder(publicBaseUrl);
            String path = StringUtils.isBlank(source.getRawPath()) ? "" : source.getRawPath();
            if (!path.startsWith("/")) {
                builder.append('/');
            }
            builder.append(path);
            if (StringUtils.isNotBlank(source.getRawQuery())) {
                builder.append('?').append(source.getRawQuery());
            }
            if (StringUtils.isNotBlank(source.getRawFragment())) {
                builder.append('#').append(source.getRawFragment());
            }
            return base.resolve(builder.toString()).toString();
        } catch (IllegalArgumentException ignored) {
            return storageUrl;
        }
    }

    private static boolean isPresignedUrl(URI uri) {
        String query = uri.getRawQuery();
        return StringUtils.isNotBlank(query) && (
            query.contains("X-Amz-Signature=")
                || query.contains("X-Amz-Credential=")
                || query.contains("Signature=")
        );
    }

    private static String normalizeBaseUrl(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        String trimmed = value.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static String blankToEmpty(String value) {
        return value == null ? "" : value;
    }
}
