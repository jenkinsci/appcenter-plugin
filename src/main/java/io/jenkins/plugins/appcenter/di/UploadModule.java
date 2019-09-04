package io.jenkins.plugins.appcenter.di;

import dagger.Module;
import dagger.Provides;
import hudson.model.Run;
import io.jenkins.plugins.appcenter.AppCenterRecorder;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;

import javax.inject.Singleton;

@Module
final class UploadModule {

    @Provides
    @Singleton
    static UploadRequest provideUploadRequest(AppCenterRecorder appCenterRecorder, Run<?, ?> run) {
        return new UploadRequest(
            appCenterRecorder.getOwnerName(),
            appCenterRecorder.getAppName(),
            appCenterRecorder.getPathToApp(),
            appCenterRecorder.getDistributionGroups()
        );
    }
}