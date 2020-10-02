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
        return new UploadRequest.Builder()
            .setOwnerName(envVars.expand(appCenterRecorder.getOwnerName()))
            .setAppName(envVars.expand(appCenterRecorder.getAppName()))
            .setPathToApp(envVars.expand(appCenterRecorder.getPathToApp()))
            .setDestinationGroups(envVars.expand(appCenterRecorder.getDistributionGroups()))
            .setReleaseNotes(envVars.expand(appCenterRecorder.getReleaseNotes()))
            .setPathToReleaseNotes(envVars.expand(appCenterRecorder.getPathToReleaseNotes()))
            .setNotifyTesters(appCenterRecorder.getNotifyTesters())
            .setBuildVersion(envVars.expand(appCenterRecorder.getBuildVersion()))
            .setPathToDebugSymbols(envVars.expand(appCenterRecorder.getPathToDebugSymbols()))
            .setCommitHash(envVars.expand(appCenterRecorder.getCommitHash()))
            .setBranchName(envVars.expand(appCenterRecorder.getBranchName()))
            .build();
    }
}