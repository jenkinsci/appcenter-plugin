package io.jenkins.plugins.appcenter;

import hudson.*;
import hudson.model.*;
import hudson.scm.ChangeLogSet;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.releasenotes.*;
import io.jenkins.plugins.appcenter.task.UploadTask;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import io.jenkins.plugins.appcenter.utils.Utils;
import io.jenkins.plugins.appcenter.validator.ApiTokenValidator;
import io.jenkins.plugins.appcenter.validator.AppNameValidator;
import io.jenkins.plugins.appcenter.validator.DistributionGroupsValidator;
import io.jenkins.plugins.appcenter.validator.PathToAppValidator;
import io.jenkins.plugins.appcenter.validator.UsernameValidator;
import io.jenkins.plugins.appcenter.validator.Validator;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class AppCenterRecorder extends Recorder implements SimpleBuildStep {

    @Nonnull
    private final Secret apiToken;

    @Nonnull
    private final String ownerName;

    @Nonnull
    private final String appName;

    @Nonnull
    private final String distributionGroups;

    @Nonnull
    private final String pathToApp;

    @Nonnull
    private final ReleaseNotes releaseNotesMethod;

    @Nullable
    private URL baseUrl;

    @DataBoundConstructor
    public AppCenterRecorder(@Nullable String apiToken, @Nullable String ownerName, @Nullable String appName, @Nullable String distributionGroups, @Nullable String pathToApp, ReleaseNotes releaseNotesMethod) {
        this.apiToken = Secret.fromString(apiToken);
        this.ownerName = Util.fixNull(ownerName);
        this.appName = Util.fixNull(appName);
        this.distributionGroups = Util.fixNull(distributionGroups);
        this.pathToApp = Util.fixNull(pathToApp);
        this.releaseNotesMethod = releaseNotesMethod;
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
    public String getDistributionGroups() {
        return distributionGroups;
    }

    @Nonnull
    public String getPathToApp() {
        return pathToApp;
    }

    @Nonnull
    public ReleaseNotes getReleaseNotesMethod() { return releaseNotesMethod; }

    @Nullable
    public URL getBaseUrl() {
        return baseUrl;
    }

    /**
     * Only meant for testing as we need to override the default base url to send requests to our mock web server for
     * tests.
     */
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
            final AppCenterServiceFactory appCenterServiceFactory = new AppCenterServiceFactory(getApiToken(), getBaseUrl(), Jenkins.get().proxy);
            final UploadRequest uploadRequest = new UploadRequest(
                getOwnerName(),
                getAppName(),
                getPathToApp(),
                getDistributionGroups(),
                fetchReleaseNotes(run, filePath, run.getEnvironment(taskListener))
            );

            return filePath.act(new UploadTask(filePath, taskListener, appCenterServiceFactory, uploadRequest));
        } catch (AppCenterException e) {
            logger.println(e.toString());
            return false;
        }
    }

    private String fetchReleaseNotes(Run<?, ?> build, FilePath filePath, EnvVars vars) {
        File tempDir;
        try {
            tempDir = File.createTempFile("jtf", null);
            if (tempDir.delete() && tempDir.mkdirs()) {
                if (releaseNotesMethod instanceof ManualReleaseNotes) {
                    ManualReleaseNotes manualReleaseNotes = (ManualReleaseNotes) releaseNotesMethod;
                    if (((ManualReleaseNotes) releaseNotesMethod).getReleaseNotes() != null) {
                        return manualReleaseNotes.getReleaseNotes();
                    }
                } else if (releaseNotesMethod instanceof FileReleaseNotes) {
                    FileReleaseNotes fileReleaseNotes = (FileReleaseNotes) releaseNotesMethod;
                    if (fileReleaseNotes.getFileName() != null) {
                        try {
                            return readReleaseNotesFile(filePath, vars.expand(fileReleaseNotes.getFileName()), tempDir);
                        } catch (Exception e) {
                            return "";
                        }
                    }
                } else {
                    StringBuilder sb = new StringBuilder();

                    ChangeLogSet<? extends ChangeLogSet.Entry> changeLogSet;
                    if (build instanceof AbstractBuild) {
                        changeLogSet = ((AbstractBuild) build).getChangeSet();
                    } else if (build instanceof WorkflowRun) {
                        List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSetList = ((WorkflowRun) build).getChangeSets();
                        changeLogSet = changeLogSetList.isEmpty() ? null : changeLogSetList.get(0);
                    } else {
                        changeLogSet = getChangeLogSetFromRun(build);
                    }
                    if (changeLogSet != null && !changeLogSet.isEmptySet()) {
                        boolean hasManyChangeSets = changeLogSet.getItems().length > 1;
                        for (ChangeLogSet.Entry entry : changeLogSet) {
                            sb.append("\n");
                            if (hasManyChangeSets) {
                                sb.append("* ");
                            }
                            sb.append(entry.getAuthor()).append(": ").append(entry.getMsg());
                        }
                    }
                    return sb.toString();
                }
            }
        } catch (IOException e) {
            return "";
        }
        return "";
    }

    private ChangeLogSet<? extends ChangeLogSet.Entry> getChangeLogSetFromRun(Run<?, ?> build) {
        ItemGroup<?> ig = build.getParent().getParent();
        for (Item item : ig.getItems()) {
            if (!item.getFullDisplayName().equals(build.getFullDisplayName())
                && !item.getFullDisplayName().equals(build.getParent().getFullDisplayName())) {
                continue;
            }

            for (Job<?, ?> job : item.getAllJobs()) {
                if (job instanceof AbstractProject<?, ?>) {
                    AbstractProject<?, ?> p = (AbstractProject<?, ?>) job;
                    return p.getBuilds().getLastBuild().getChangeSet();
                }
            }
        }
        return null;
    }

    private String readReleaseNotesFile(FilePath workingDir, String strFile, File tempDir) throws IOException, InterruptedException {
        if (strFile.startsWith(workingDir.getRemote())) {
            strFile = strFile.substring(workingDir.getRemote().length() + 1);
        }
        File file;

        if (workingDir.isRemote()) {
            FilePath remoteFile = new FilePath(workingDir, strFile);
            file = new File(tempDir, remoteFile.getName());
            if (file.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    remoteFile.copyTo(fos);
                }
            }
        } else {
            file = new File(workingDir.getRemote(), strFile);
        }
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return IOUtils.toString(inputStream, "UTF-8");
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
        public List<ReleaseNotesDescriptor> getReleaseNotesMethodList() {
            List<ReleaseNotesDescriptor> releaseNotesMethods = new ArrayList<ReleaseNotesDescriptor>(4);
            releaseNotesMethods.add((ReleaseNotesDescriptor) Utils.getDescriptorOrDie(NoReleaseNotes.class));
            releaseNotesMethods.add((ReleaseNotesDescriptor) Utils.getDescriptorOrDie(ChangelogReleaseNotes.class));
            releaseNotesMethods.add((ReleaseNotesDescriptor) Utils.getDescriptorOrDie(FileReleaseNotes.class));
            releaseNotesMethods.add((ReleaseNotesDescriptor) Utils.getDescriptorOrDie(ManualReleaseNotes.class));
            return releaseNotesMethods;
        }

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