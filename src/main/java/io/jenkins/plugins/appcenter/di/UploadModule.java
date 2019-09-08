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
        // TODO: Expand the environment variable for all parameters
        return new UploadRequest(
            appCenterRecorder.getOwnerName(),
            appCenterRecorder.getAppName(),
            envVars.expand(appCenterRecorder.getPathToApp()),
            appCenterRecorder.getDistributionGroups()
        );
    }
}