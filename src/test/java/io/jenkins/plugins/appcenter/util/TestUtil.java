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
        return createFileForFreeStyle(pathToFile, "all of us with wings");
    }

    public static TestBuilder createFileForFreeStyle(final @Nonnull String pathToFile, final @Nonnull String content) {
        return new TestAppWriter(pathToFile, content);
    }

    private static class TestAppWriter extends TestBuilder {

        @Nonnull
        private final String pathToFile;
        @Nonnull
        private final String content;

        private TestAppWriter(final @Nonnull String pathToFile, final @Nonnull String content) {
            this.pathToFile = pathToFile;
            this.content = content;
        }

        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
            Objects.requireNonNull(build.getWorkspace()).child(pathToFile).write(content, "UTF-8");
            return true;
        }
    }
}