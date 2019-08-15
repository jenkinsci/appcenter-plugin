package io.jenkins.plugins.appcenter.releasenotes;

import hudson.Extension;
import hudson.model.Descriptor;
import io.jenkins.plugins.appcenter.Messages;
import io.jenkins.plugins.appcenter.utils.Utils;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import javax.annotation.Nonnull;

public class ChangelogReleaseNotes extends ReleaseNotes {

    @DataBoundConstructor
    public ChangelogReleaseNotes() { }

    @Override
    public Descriptor<ReleaseNotes> getDescriptor() throws IllegalStateException {
        return Utils.getDescriptorOrDie(this.getClass());
    }

    @Symbol("changelog")
    @Extension
    public static class DescriptorImpl extends ReleaseNotesDescriptor<ChangelogReleaseNotes> {

        public DescriptorImpl() {
            super();
            load();
        }

        @Override
        public String getDisplayName() {
            return Messages.ChangeLogReleaseNotes_DescriptorImpl_DisplayName();
        }

        @Override
        public ReleaseNotes newInstance(StaplerRequest req, @Nonnull JSONObject formData) {
            return new ChangelogReleaseNotes();
        }
    }
}