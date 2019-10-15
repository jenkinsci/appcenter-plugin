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
            "at-this-moment-you-should-be-with-us",
            "janes-addiction",
            "ritual-de-lo-habitual",
            "three/days/xiola.ipa",
            "three/days/symbols.zip", "casey, niccoli"
        );

        // When
        jenkinsRule.configRoundtrip(appCenterRecorder);

        // Then
        assertThat(appCenterRecorder.getApiToken()).isEqualTo(Secret.fromString("at-this-moment-you-should-be-with-us"));
        assertThat(appCenterRecorder.getOwnerName()).isEqualTo("janes-addiction");
        assertThat(appCenterRecorder.getAppName()).isEqualTo("ritual-de-lo-habitual");
        assertThat(appCenterRecorder.getPathToApp()).isEqualTo("three/days/xiola.ipa");
        assertThat(appCenterRecorder.getPathToDebugSymbols()).isEqualTo("three/days/symbols.zip");
        assertThat(appCenterRecorder.getDistributionGroups()).isEqualTo("casey, niccoli");
    }
}