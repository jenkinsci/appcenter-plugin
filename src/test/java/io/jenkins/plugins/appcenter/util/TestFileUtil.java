package io.jenkins.plugins.appcenter.util;

import javax.annotation.Nonnull;
import java.io.File;

public final class TestFileUtil {

    @Nonnull
    public static File createFileForTesting() {
        return new File("src/test/resources/three/days/xiola.apk");
    }

    @Nonnull
    public static File createLargeFileForTesting() {
        return new File("src/test/resources/three/days/xiola.apk") {
            @Override
            public long length() {
                return (1024 * 1024) * 512; // Double the max size allowed to upload.
            }
        };
    }
}