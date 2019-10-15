package io.jenkins.plugins.appcenter;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import hudson.model.FreeStyleProject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class ConfigurationTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    private FreeStyleProject freeStyleProject;

    @Before
    public void setUp() throws Exception {
        freeStyleProject = jenkinsRule.createFreeStyleProject();
    }

    @Test
    public void should_Configure_RequiredParameters_ViaWebForm() throws Exception {
        // Given
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("at-this-moment-you-should-be-with-us", "janes-addiction", "ritual-de-lo-habitual", "three/days/xiola.ipa", "thee/days/symbols.dsym", "casey, niccoli");
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        final HtmlForm htmlForm = jenkinsRule.createWebClient().getPage(freeStyleProject, "configure").getFormByName("config");
        jenkinsRule.submit(htmlForm);

        // When
        final AppCenterRecorder configuredAppCenterRecorder = freeStyleProject.getPublishersList().get(AppCenterRecorder.class);

        // Then
        jenkinsRule.assertEqualDataBoundBeans(appCenterRecorder, configuredAppCenterRecorder);
    }

    @Test
    public void should_Configure_OptionalReleaseNotes_ViaWebForm() throws Exception {
        // Given
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("at-this-moment-you-should-be-with-us", "janes-addiction", "ritual-de-lo-habitual", "three/days/xiola.ipa", "three/days/symbols.dsym", "casey, niccoli");
        appCenterRecorder.setReleaseNotes("I miss you my dear Xiola");
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        final HtmlForm htmlForm = jenkinsRule.createWebClient().getPage(freeStyleProject, "configure").getFormByName("config");
        jenkinsRule.submit(htmlForm);

        // When
        final AppCenterRecorder configuredAppCenterRecorder = freeStyleProject.getPublishersList().get(AppCenterRecorder.class);

        // Then
        jenkinsRule.assertEqualDataBoundBeans(appCenterRecorder, configuredAppCenterRecorder);
    }

    @Test
    public void should_Configure_OptionalNotifyTesters_ViaWebForm() throws Exception {
        // Given
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("at-this-moment-you-should-be-with-us", "janes-addiction", "ritual-de-lo-habitual", "three/days/xiola.ipa", "three/days/symbols.dsym", "casey, niccoli");
        appCenterRecorder.setNotifyTesters(false);
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        final HtmlForm htmlForm = jenkinsRule.createWebClient().getPage(freeStyleProject, "configure").getFormByName("config");
        jenkinsRule.submit(htmlForm);

        // When
        final AppCenterRecorder configuredAppCenterRecorder = freeStyleProject.getPublishersList().get(AppCenterRecorder.class);

        // Then
        jenkinsRule.assertEqualDataBoundBeans(appCenterRecorder, configuredAppCenterRecorder);
    }
}