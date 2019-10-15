package io.jenkins.plugins.appcenter.task.request;

import javax.annotation.Nonnull;
import java.io.Serializable;

public final class UploadRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Nonnull
    public final String ownerName;
    @Nonnull
    public final String appName;
    @Nonnull
    public final String pathToApp;
    @Nonnull
    public final String pathToDebugSymbols;
    @Nonnull
    public final String destinationGroups;
    @Nonnull
    public final String releaseNotes;

    public final boolean notifyTesters;

    public UploadRequest(@Nonnull String ownerName, @Nonnull String appName, @Nonnull String pathToApp, @Nonnull String pathToDebugSymbols, @Nonnull String destinationGroups, @Nonnull String releaseNotes, boolean notifyTesters) {
        this.ownerName = ownerName;
        this.appName = appName;
        this.pathToApp = pathToApp;
        this.destinationGroups = destinationGroups;
        this.releaseNotes = releaseNotes;
        this.notifyTesters = notifyTesters;
        this.pathToDebugSymbols = pathToDebugSymbols;
    }
}