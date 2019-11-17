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
import io.jenkins.plugins.appcenter.di.AppCenterComponent;
import io.jenkins.plugins.appcenter.di.DaggerAppCenterComponent;
import io.jenkins.plugins.appcenter.task.UploadTask;
import io.jenkins.plugins.appcenter.validator.ApiTokenValidator;
import io.jenkins.plugins.appcenter.validator.AppNameValidator;
import io.jenkins.plugins.appcenter.validator.DistributionGroupsValidator;
import io.jenkins.plugins.appcenter.validator.PathPlaceholderValidator;
import io.jenkins.plugins.appcenter.validator.PathToAppValidator;
import io.jenkins.plugins.appcenter.validator.UsernameValidator;
import io.jenkins.plugins.appcenter.validator.Validator;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintStream;

import static hudson.model.Result.FAILURE;
import static hudson.model.Result.SUCCESS;

@SuppressWarnings("unused")
public final class AppCenterRecorder extends Recorder implements SimpleBuildStep {

    @Nonnull
    private final Secret apiToken;

    @Nonnull
    private final String ownerName;

    @Nonnull
    private final String appName;

    @Nonnull
    private final String pathToApp;

    @Nonnull
    private final String distributionGroups;

    @Nullable
    private String releaseNotes;

    private boolean notifyTesters = true;

    @Nullable
    private transient String baseUrl;

    @DataBoundConstructor
    public AppCenterRecorder(@Nullable String apiToken, @Nullable String ownerName, @Nullable String appName, @Nullable String pathToApp, @Nullable String distributionGroups) {
        this.apiToken = Secret.fromString(apiToken);
        this.ownerName = Util.fixNull(ownerName);
        this.appName = Util.fixNull(appName);
        this.pathToApp = Util.fixNull(pathToApp);
        this.distributionGroups = Util.fixNull(distributionGroups);
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
    public String getPathToApp() {
        return pathToApp;
    }

    @Nonnull
    public String getDistributionGroups() {
        return distributionGroups;
    }

    @Nonnull
    public String getReleaseNotes() {
        return Util.fixNull(releaseNotes);
    }

    public boolean getNotifyTesters() {
        return notifyTesters;
    }

    @DataBoundSetter
    public void setReleaseNotes(@Nullable String releaseNotes) {
        this.releaseNotes = Util.fixEmpty(releaseNotes);
    }

    @DataBoundSetter
    public void setNotifyTesters(boolean notifyTesters) {
        this.notifyTesters = notifyTesters;
    }

    /**
     * Do not use outside of testing.
     *
     * @param baseUrl String Sets a new base url
     */
    public void setBaseUrl(@Nullable String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
        final Result buildResult = run.getResult();
        if (buildResult != null && buildResult.isWorseOrEqualTo(FAILURE)) {
            taskListener.getLogger().println(Messages.AppCenterRecorder_DescriptorImpl_errors_upstreamBuildFailure());
            return;
        }

        if (uploadToAppCenter(run, filePath, taskListener)) {
            run.setResult(SUCCESS);
        } else {
            run.setResult(FAILURE);
        }
    }

    private boolean uploadToAppCenter(Run<?, ?> run, FilePath filePath, TaskListener taskListener) throws IOException, InterruptedException {
        final AppCenterComponent component = DaggerAppCenterComponent.factory().create(this, Jenkins.get(), run, filePath, taskListener, baseUrl);
        final UploadTask uploadTask = component.uploadTask();
        final PrintStream logger = taskListener.getLogger();

        try {
            return filePath.act(uploadTask);
        } catch (AppCenterException e) {
            e.printStackTrace(logger);
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
        public FormValidation doCheckPathToApp(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_missingPathToApp());
            }

            final Validator pathToAppValidator = new PathToAppValidator();

            if (!pathToAppValidator.isValid(value)) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_invalidPathToApp());
            }

            final Validator pathPlaceholderValidator = new PathPlaceholderValidator();

            if (!pathPlaceholderValidator.isValid(value)) {
                return FormValidation.warning(Messages.AppCenterRecorder_DescriptorImpl_warnings_mustNotStartWithEnvVar());
            }

            return FormValidation.ok();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckDistributionGroups(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_missingDistributionGroups());
            }

            final Validator validator = new DistributionGroupsValidator();

            if (!validator.isValid(value)) {
                return FormValidation.error(Messages.AppCenterRecorder_DescriptorImpl_errors_invalidDistributionGroups());
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