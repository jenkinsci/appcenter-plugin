package io.jenkins.plugins.appcenter.util;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;

public final class ParserFactory {

    @Inject
    ParserFactory() {
    }

    @Nonnull
    public AndroidParser androidParser(final @Nonnull File file) {
        return new AndroidParser(file);
    }
}