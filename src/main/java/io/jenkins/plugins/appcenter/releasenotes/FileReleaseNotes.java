package io.jenkins.plugins.appcenter.releasenotes;

import hudson.Extension;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import io.jenkins.plugins.appcenter.Messages;
import io.jenkins.plugins.appcenter.utils.Utils;
import io.jenkins.plugins.appcenter.validator.PathToAppValidator;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.export.Exported;

public class FileReleaseNotes extends ReleaseNotes {

    @Exported
    private String fileName;

    @DataBoundConstructor
    public FileReleaseNotes(String fileName) {
        this.fileName = Util.fixEmptyAndTrim(fileName);
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public Descriptor<ReleaseNotes> getDescriptor() throws IllegalStateException {
        return Utils.getDescriptorOrDie(this.getClass());
    }

    @Symbol("file")
    @Extension
    public static class DescriptorImpl extends ReleaseNotesDescriptor<FileReleaseNotes> {

        public DescriptorImpl() {
            super();
            load();
        }

        @Override
        public String getDisplayName() {
            return Messages.FileReleaseNotes_DescriptorImpl_DisplayName();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckFileName(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.FileReleaseNotes_DescriptorImpl_errors_missingPathToFile());
            }

            if (!(new PathToAppValidator()).isValid(value)) {
                return FormValidation.error(Messages.FileReleaseNotes_DescriptorImpl_errors_invalidPathToFile());
            }

            return FormValidation.ok();
        }
    }
}