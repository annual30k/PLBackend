package org.dromara.patrol.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static org.assertj.core.api.Assertions.assertThat;

class OssAccessUrlResolverTest {

    @Tag("dev")
    @Test
    void externalUrlUsesConfiguredPublicBaseAndPreservesObjectPath() {
        OssAccessUrlResolver resolver = new OssAccessUrlResolver("http://192.168.11.157:9000/");

        String url = resolver.toExternalUrl("http://minio:9000/patrol/avatar/officer.png");

        assertThat(url).isEqualTo("http://192.168.11.157:9000/patrol/avatar/officer.png");
    }

    @Tag("dev")
    @Test
    void externalUrlDoesNotRewritePresignedUrls() {
        OssAccessUrlResolver resolver = new OssAccessUrlResolver("https://files.example.com");

        String url = resolver.toExternalUrl("http://minio:9000/patrol/avatar/officer.png?X-Amz-Expires=120&X-Amz-Signature=abc");

        assertThat(url).isEqualTo("http://minio:9000/patrol/avatar/officer.png?X-Amz-Expires=120&X-Amz-Signature=abc");
    }

    @Tag("dev")
    @Test
    void externalUrlLeavesOriginalWhenPublicBaseIsBlank() {
        OssAccessUrlResolver resolver = new OssAccessUrlResolver("");

        String url = resolver.toExternalUrl("http://minio:9000/patrol/avatar/officer.png");

        assertThat(url).isEqualTo("http://minio:9000/patrol/avatar/officer.png");
    }
}
