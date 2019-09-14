package io.jenkins.plugins.appcenter.util;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.jvnet.hudson.test.TestBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

public final class TestUtil {
    public static TestBuilder createFileForFreeStyle(final @Nonnull String pathToFile) {
        return new TestAppWriter(pathToFile);
    }

    public static TestBuilder createFileForPipeline(final @Nonnull String pathToFile) {
        return new TestAppWriter(pathToFile);
    }

    private static class TestAppWriter extends TestBuilder {

        @Nonnull
        private final String pathToFile;

        private TestAppWriter(final @Nonnull String pathToFile) {
            this.pathToFile = pathToFile;
        }

        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
            Objects.requireNonNull(build.getWorkspace()).child(pathToFile).write("all of us with wings", "UTF-8");
            return true;
        }
    }
}