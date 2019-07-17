package io.jenkins.plugins.appcenter.validator;

import javax.annotation.Nonnull;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class UsernameValidator extends Validator {

    @Nonnull
    @Override
    protected Predicate<String> predicate() {
        return Pattern.compile("^(?![a-zA-Z0-9-_.]*+$)")
                .asPredicate()
                .negate();
    }

}