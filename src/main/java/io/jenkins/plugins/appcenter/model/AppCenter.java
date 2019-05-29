package io.jenkins.plugins.appcenter.model;

import hudson.util.Secret;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.net.URL;

public abstract class AppCenter implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String APPCENTER_BASE_URL = "https://api.appcenter.ms/";

    private final Secret apiToken;
    @Nullable
    private final URL baseUrl;

    AppCenter(Secret apiToken, @Nullable URL baseUrl) {
        this.apiToken = apiToken;
        this.baseUrl = baseUrl;
    }

    public Secret getApiToken() {
        return apiToken;
    }

    public String getBaseUrl() {
        if (baseUrl == null) {
            return APPCENTER_BASE_URL;
        } else {
            return baseUrl.toString();
        }
    }
}
