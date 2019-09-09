package io.jenkins.plugins.appcenter.di;

import dagger.Module;
import dagger.Provides;
import hudson.EnvVars;
import hudson.ProxyConfiguration;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.PrintStream;

@Module
final class JenkinsModule {

    @Provides
    @Nullable
    @Singleton
    static ProxyConfiguration provideProxyConfiguration(Jenkins jenkins) {
        return jenkins.proxy;
    }

    @Provides
    @Singleton
    static EnvVars provideEnvVars(Run<?, ?> run, TaskListener taskListener) throws RuntimeException {
        final PrintStream logger = taskListener.getLogger();
        try {
            return run.getEnvironment(taskListener);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(logger);
            throw new RuntimeException("Failed to get Environment Variables.");
        }
    }
}