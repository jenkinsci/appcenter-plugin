package io.jenkins.plugins.appcenter.task;

import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.remote.AppCenterService;
import io.jenkins.plugins.appcenter.remote.UploadService;
import jenkins.security.MasterToSlaveCallable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

abstract class AppCenterTask extends MasterToSlaveCallable<Boolean, AppCenterException> {

    private final AppCenterServiceFactory appCenterServiceFactory;
    AppCenterService appCenterService;
    UploadService uploadService;

    AppCenterTask(final AppCenterServiceFactory appCenterServiceFactory) {
        this.appCenterServiceFactory = appCenterServiceFactory;
    }

    @Override
    public final Boolean call() throws AppCenterException {
        try {
            createServices();
            return execute();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new AppCenterException(e);
        }
    }

    private void createServices() {
        appCenterService = appCenterServiceFactory.createAppCenterService();
        uploadService = appCenterServiceFactory.createUploadService();
    }

    protected abstract Boolean execute() throws IOException, InterruptedException, AppCenterException, ExecutionException;
}