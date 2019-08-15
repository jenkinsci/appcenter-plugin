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

public class NoReleaseNotes extends ReleaseNotes {

    @DataBoundConstructor
    public NoReleaseNotes() { }

    @Override
    public Descriptor<ReleaseNotes> getDescriptor() throws IllegalStateException {
        return Utils.getDescriptorOrDie(this.getClass());
    }

    @Symbol("none")
    @Extension
    public static class DescriptorImpl extends ReleaseNotesDescriptor<NoReleaseNotes> {

        public DescriptorImpl() {
            super();
            load();
        }

        @Override
        public String getDisplayName() {
            return Messages.NoReleaseNotes_DescriptorImpl_DisplayName();
        }

        @Override
        public ReleaseNotes newInstance(StaplerRequest req, @Nonnull JSONObject formData) {
            return new NoReleaseNotes();
        }
    }
}