package io.jenkins.plugins.appcenter.model;

import hudson.util.Secret;

import java.net.URL;

public final class Upload extends AppCenter {

    private final String ownerName;
    private final String appName;
    private final String pathToApp;

    public Upload(Secret apiToken, String ownerName, String appName, String pathToApp, URL baseUrl) {
        super(apiToken, baseUrl);
        this.ownerName = ownerName;
        this.appName = appName;
        this.pathToApp = pathToApp;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPathToApp() {
        return pathToApp;
    }
}
