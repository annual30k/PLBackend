package org.dromara.patrol.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Authenticates machine-to-machine requests from the edge cerebellum service.
 */
@Component
public class CerebellumAccessGuard {

    private final String sharedToken;

    public CerebellumAccessGuard(@Value("${patrol.cerebellum.token:}") String sharedToken) {
        this.sharedToken = sharedToken == null ? "" : sharedToken.trim();
    }

    public boolean isAllowed(HttpServletRequest request) {
        if (!StringUtils.hasText(sharedToken)) {
            return false;
        }
        String providedToken = resolveToken(request);
        return constantTimeEquals(sharedToken, providedToken);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("X-Cerebellum-Token");
        if (StringUtils.hasText(token)) {
            return token.trim();
        }
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization)) {
            String value = authorization.trim();
            if (value.regionMatches(true, 0, "Bearer ", 0, 7)) {
                return value.substring(7).trim();
            }
            return value;
        }
        return "";
    }

    private boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(
            expected.getBytes(StandardCharsets.UTF_8),
            (actual == null ? "" : actual).getBytes(StandardCharsets.UTF_8)
        );
    }
}
