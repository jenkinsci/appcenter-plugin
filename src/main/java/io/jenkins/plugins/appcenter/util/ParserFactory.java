package io.jenkins.plugins.appcenter.util;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;

public final class ParserFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    ParserFactory() {
    }

    @Nonnull
    public AndroidParser androidParser(final @Nonnull File file) {
        return new AndroidParser(file);
    }
}