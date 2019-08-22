package io.jenkins.plugins.appcenter.validator;

import javax.annotation.Nonnull;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class PathPlaceholderValidator extends Validator {

    @Nonnull
    @Override
    protected Predicate<String> predicate() {
        return Pattern.compile("^\\$\\{[^}]+}")
                .asPredicate()
                .negate();
    }

}