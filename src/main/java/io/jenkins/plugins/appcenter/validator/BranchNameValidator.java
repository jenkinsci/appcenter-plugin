package io.jenkins.plugins.appcenter.validator;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public final class BranchNameValidator extends Validator {

    @Nonnull
    @Override
    protected Predicate<String> predicate() {
        return value -> !value.contains(" ");
    }

}