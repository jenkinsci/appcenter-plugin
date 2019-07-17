package io.jenkins.plugins.appcenter.validator;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public abstract class Validator {

    @Nonnull
    protected abstract Predicate<String> predicate();

    public boolean isValid(@Nonnull String value) {
        return predicate().test(value);
    }
}