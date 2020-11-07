package io.jenkins.plugins.appcenter.util;

import hudson.FilePath;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;

public class RemoteFileUtils implements Serializable {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final FilePath filePath;

    @Inject
    RemoteFileUtils(@Nonnull final FilePath filePath) {
        this.filePath = filePath;
    }

    @Nonnull
    public File getRemoteFile(@Nonnull String pathToRemoteFile) {
        return new File(filePath.child(pathToRemoteFile).getRemote());
    }
}