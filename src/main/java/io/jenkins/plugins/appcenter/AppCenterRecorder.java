package io.jenkins.plugins.appcenter;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.remote.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.task.UploadTask;
import io.jenkins.plugins.appcenter.validator.*;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

@SuppressWarnings("unused")
public final class AppCenterRecorder extends Recorder implements SimpleBuildStep {

    @Nonnull
    private final Secret apiToken;

    @Nonnull
    private final String ownerName;

    @Nonnull
    private final String appName;

    @Nonnull
    private final String distributionGroup;

    @Nonnull
    private final String pathToApp;

    @Nullable
    private URL baseUrl;

    @DataBoundConstructor
    public AppCenterRecorder(@Nullable String apiToken, @Nullable String ownerName, @Nullable String appName, @Nullable String distributionGroup, @Nullable String pathToApp) {
        this.apiToken = Secret.fromString(apiToken);
        this.ownerName = Util.fixNull(ownerName);
        this.appName = Util.fixNull(appName);
        this.distributionGroup = Util.fixNull(distributionGroup);
        this.pathToApp = Util.fixNull(pathToApp);
    }

    @Nonnull
    public Secret getApiToken() {
        return apiToken;
    }

    @Nonnull
    public String getOwnerName() {
        return ownerName;
    }

    @Nonnull
    public String getAppName() {
        return appName;
    }

    @Nonnull
    public String getDistributionGroup() {
        return distributionGroup;
    }

    @Nonnull
    public String getPathToApp() {
        return pathToApp;
    }

    @Nullable
    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(@Nullable URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
        if (uploadToAppCenter(run, filePath, taskListener)) {
            run.setResult(Result.SUCCESS);
        } else {
            run.setResult(Result.FAILURE);
        }
    }

    private boolean uploadToAppCenter(Run<?, ?> run, FilePath filePath, TaskListener taskListener) throws IOException, InterruptedException {
        final PrintStream logger = taskListener.getLogger();

        try {
            final AppCenterServiceFactory appCenterServiceFactory = new AppCenterServiceFactory(
                    getApiToken(), getOwnerName(), getAppName(), getDistributionGroup(), getPathToApp(), getBaseUrl()
            );

            return filePath.act(new UploadTask(filePath, taskListener, appCenterServiceFactory));
        } catch (AppCenterException e) {
            logger.println(e.toString());
            return false;
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Symbol("appCenter")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @SuppressWarnings("unused")
        public FormValidation doCheckApiToken(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_missingApiToken());
            }

            final Validator validator = new ApiTokenValidator();

            if (!validator.isValid(value)) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_invalidApiToken());
            }

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckOwnerName(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_missingOwnerName());
            }

            // TODO proper validator, every non-empty string should be valid?
            final Validator validator = new UsernameValidator();

            if (!validator.isValid(value)) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_invalidOwnerName());
            }

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckAppName(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_missingAppName());
            }

            final Validator validator = new AppNameValidator();

            if (!validator.isValid(value)) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_invalidAppName());
            }

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckDistributionGroup(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_missingDistributionGroup());
            }

            final Validator validator = new DistributionGroupValidator();

            if (!validator.isValid(value)) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_invalidDistributionGroup());
            }

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckPathToApp(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_missingPathToApp());
            }

            final Validator validator = new PathToAppValidator();

            if (!validator.isValid(value)) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_invalidPathToApp());
            }

            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.AppCenterRecorder_DescriptorImpl_DisplayName();
        }

    }
}
