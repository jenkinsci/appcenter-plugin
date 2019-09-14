package io.jenkins.plugins.appcenter.di;

import dagger.Module;
import dagger.Provides;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterRecorder;

import javax.inject.Singleton;

@Module
final class AuthModule {
    @Provides
    @Singleton
    static Secret provideApiToken(AppCenterRecorder appCenterRecorder) {
        return appCenterRecorder.getApiToken();
    }
}