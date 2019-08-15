package io.jenkins.plugins.appcenter.releasenotes;

import hudson.model.Descriptor;
import hudson.model.Saveable;

public abstract class ReleaseNotesDescriptor<T extends ReleaseNotes> extends Descriptor<ReleaseNotes> implements Saveable { }