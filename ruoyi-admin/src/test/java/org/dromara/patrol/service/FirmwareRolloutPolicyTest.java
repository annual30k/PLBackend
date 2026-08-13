package org.dromara.patrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FirmwareRolloutPolicyTest {

    @Tag("dev")
    @Test
    void allScopeMatchesEveryDevice() {
        assertTrue(FirmwareRolloutPolicy.matches("ALL", "", "DEV-001"));
        assertTrue(FirmwareRolloutPolicy.matches(null, null, "DEV-002"));
    }

    @Tag("dev")
    @Test
    void deviceScopeOnlyMatchesConfiguredTargets() {
        assertTrue(FirmwareRolloutPolicy.matches("devices", "dev-001， DEV-002\ndev-003", "DEV-002"));
        assertFalse(FirmwareRolloutPolicy.matches("DEVICES", "DEV-001,DEV-003", "DEV-002"));
        assertFalse(FirmwareRolloutPolicy.matches("DEVICES", "", "DEV-001"));
    }

    @Tag("dev")
    @Test
    void targetNormalizationIsStableAndDeduplicated() {
        assertEquals("DEV-001,DEV-002", FirmwareRolloutPolicy.normalizeTargets(" dev-001 ; DEV-002,dev-001 "));
        assertTrue(FirmwareRolloutPolicy.isSupportedScope("all"));
        assertFalse(FirmwareRolloutPolicy.isSupportedScope("DEPARTMENTS"));
    }
}
