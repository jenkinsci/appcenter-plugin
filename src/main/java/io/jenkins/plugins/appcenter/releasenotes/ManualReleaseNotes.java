package io.jenkins.plugins.appcenter.releasenotes;

import hudson.Extension;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import io.jenkins.plugins.appcenter.Messages;
import io.jenkins.plugins.appcenter.utils.Utils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.export.Exported;

public class ManualReleaseNotes extends ReleaseNotes {

    @Exported
    private String releaseNotes;

    @DataBoundConstructor
    public ManualReleaseNotes(String releaseNotes) {
        this.releaseNotes = Util.fixEmptyAndTrim(releaseNotes);
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    @Override
    public Descriptor<ReleaseNotes> getDescriptor() throws IllegalStateException {
        return Utils.getDescriptorOrDie(this.getClass());
    }

    @Symbol("manual")
    @Extension
    public static class DescriptorImpl extends ReleaseNotesDescriptor<ManualReleaseNotes> {

        public DescriptorImpl() {
            super();
            load();
        }

        @Override
        public String getDisplayName() {
            return Messages.ManualReleaseNotes_DescriptorImpl_DisplayName();
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckReleaseNotes(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.ManualReleaseNotes_DescriptorImpl_errors_missingReleaseNotes());
            } else {
                return FormValidation.ok();
            }
        }
    }
}