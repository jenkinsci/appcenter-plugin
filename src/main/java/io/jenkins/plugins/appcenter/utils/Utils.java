package io.jenkins.plugins.appcenter.utils;

import hudson.model.Describable;
import hudson.model.Descriptor;
import io.jenkins.plugins.appcenter.Messages;
import jenkins.model.Jenkins;

public class Utils {

    private static Jenkins getJenkinsInstance() throws IllegalStateException {
        Jenkins instance = Jenkins.getInstanceOrNull();
        if (instance == null) {
            throw new IllegalStateException(Messages.Global_errors_JenkinsMissingInstance());
        }
        return instance;
    }

    public static <T extends Describable<T>> Descriptor<T> getDescriptorOrDie(Class<? extends T> type) {
        @SuppressWarnings("unchecked")
        Descriptor<T> descriptor = Utils.getJenkinsInstance().getDescriptorOrDie(type);
        if (!descriptor.isSubTypeOf(type)) {
            throw new IllegalStateException(type.toString());
        }
        return descriptor;
    }
}