package io.jenkins.plugins.appcenter.di;

import dagger.Module;
import dagger.Provides;
import hudson.EnvVars;
import io.jenkins.plugins.appcenter.AppCenterRecorder;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;

import javax.inject.Singleton;

@Module
final class UploadModule {

    @Provides
    @Singleton
    static UploadRequest provideUploadRequest(AppCenterRecorder appCenterRecorder, EnvVars envVars) {
        return new UploadRequest(
            envVars.expand(appCenterRecorder.getOwnerName()),
            envVars.expand(appCenterRecorder.getAppName()),
            envVars.expand(appCenterRecorder.getPathToApp()),
            envVars.expand(appCenterRecorder.getDistributionGroups()),
            envVars.expand(appCenterRecorder.getReleaseNotes()),
            Boolean.parseBoolean(envVars.expand(appCenterRecorder.getNotifyTesters()))
        );
    }
}
