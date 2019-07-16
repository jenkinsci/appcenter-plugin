package io.jenkins.plugins.appcenter.task.request;

import java.io.Serializable;

public final class UploadRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    public final String ownerName;
    public final String appName;
    public final String pathToApp;
    public final String destinationGroups;

    public UploadRequest(String ownerName, String appName, String pathToApp, String destinationGroups) {
        this.ownerName = ownerName;
        this.appName = appName;
        this.pathToApp = pathToApp;
        this.destinationGroups = destinationGroups;
    }
}