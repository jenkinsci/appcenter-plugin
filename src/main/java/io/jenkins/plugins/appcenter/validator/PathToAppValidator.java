package io.jenkins.plugins.appcenter.validator;

import hudson.Util;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public final class PathToAppValidator extends Validator {

    @Nonnull
    @Override
    protected Predicate<String> predicate() {
        return Util::isRelativePath;
    }
}