package org.dromara.patrol.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Shared validation and matching rules for firmware rollout scope.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FirmwareRolloutPolicy {

    public static final String ALL = "ALL";
    public static final String DEVICES = "DEVICES";
    private static final Set<String> SUPPORTED_SCOPES = Set.of(ALL, DEVICES);

    public static String normalizeScope(String scope) {
        return scope == null || scope.isBlank() ? ALL : scope.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean isSupportedScope(String scope) {
        return SUPPORTED_SCOPES.contains(normalizeScope(scope));
    }

    public static Set<String> targets(String value) {
        if (value == null || value.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(value.split("[\\s,，;；]+"))
            .map(String::trim)
            .filter(item -> !item.isBlank())
            .map(item -> item.toUpperCase(Locale.ROOT))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static String normalizeTargets(String value) {
        return String.join(",", targets(value));
    }

    public static boolean matches(String scope, String configuredTargets, String deviceId) {
        String normalizedScope = normalizeScope(scope);
        if (ALL.equals(normalizedScope)) {
            return true;
        }
        if (!DEVICES.equals(normalizedScope) || deviceId == null || deviceId.isBlank()) {
            return false;
        }
        return targets(configuredTargets).contains(deviceId.trim().toUpperCase(Locale.ROOT));
    }
}
