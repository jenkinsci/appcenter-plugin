package io.jenkins.plugins.appcenter;

import hudson.util.Secret;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static com.google.common.truth.Truth.assertThat;

public class RoundTripTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void should_Configure_AppCenterRecorder_With_Required_Inputs() throws Exception {
        // Given
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder(
            "api-token",
            "owner-name",
            "app-name",
            "distribution-group",
            "path/to/app.apk"
        );

        // When
        jenkinsRule.configRoundtrip(appCenterRecorder);

        // Then
        assertThat(appCenterRecorder.getApiToken()).isEqualTo(Secret.fromString("api-token"));
        assertThat(appCenterRecorder.getOwnerName()).isEqualTo("owner-name");
        assertThat(appCenterRecorder.getAppName()).isEqualTo("app-name");
        assertThat(appCenterRecorder.getDistributionGroups()).isEqualTo("distribution-group");
        assertThat(appCenterRecorder.getPathToApp()).isEqualTo("path/to/app.apk");
    }

}