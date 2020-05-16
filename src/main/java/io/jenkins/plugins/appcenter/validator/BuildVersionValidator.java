package io.jenkins.plugins.appcenter.validator;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

public final class BuildVersionValidator extends Validator {

    @Nonnull
    @Override
    protected Predicate<String> predicate() {
        return value -> !value.contains(" ");
    }

}