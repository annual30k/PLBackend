package org.dromara.patrol.controller;

import org.dromara.patrol.domain.PatrolMedia;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PatrolFileControllerTest {

    @Tag("dev")
    @Test
    void classpathSampleIsServedBeforeOssEvenWhenOssIdExists() {
        PatrolMedia media = new PatrolMedia();
        media.setOssId(95270003L);
        media.setObjectKey("classpath:patrol-samples/zhang-duty-photo.jpg");

        assertThat(PatrolFileController.shouldServeClasspathSample(media)).isTrue();
    }

    @Tag("dev")
    @Test
    void commandAdminCanDownloadTenantMediaWhileDeviceUsersStayOwnerScoped() {
        assertThat(PatrolFileController.downloadOwnerScope(1L, true)).isNull();
        assertThat(PatrolFileController.downloadOwnerScope(9527L, false)).isEqualTo(9527L);
    }
}
