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
    static PrintStream provideLogger(TaskListener taskListener) {
        return taskListener.getLogger();
    }

    @Provides
    @Singleton
    static EnvVars provideEnvVars(Run<?, ?> run, TaskListener taskListener, PrintStream logger) throws RuntimeException {
        try {
            return run.getEnvironment(taskListener);
        } catch (IOException | InterruptedException e) {
            logger.println(e);
            throw new RuntimeException("Failed to get Environment Variables.");
        }
    }
}