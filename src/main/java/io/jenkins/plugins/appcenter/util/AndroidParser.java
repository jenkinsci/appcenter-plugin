package io.jenkins.plugins.appcenter.util;

import net.dongliu.apk.parser.ApkParsers;
import net.dongliu.apk.parser.bean.ApkMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public final class AndroidParser {

    @Nonnull
    private final File file;
    @Nullable
    private ApkMeta apkMeta;

    AndroidParser(final @Nonnull File file) {
        this.file = file;
    }

    @Nonnull
    public String versionCode() throws IOException {
        return metaInfo().getVersionCode().toString();
    }

    @Nonnull
    public String versionName() throws IOException {
        return metaInfo().getVersionName();
    }

    @Nonnull
    public String fileName() {
        return file.getName();
    }

    @Nonnull
    private ApkMeta metaInfo() throws IOException {
        if (apkMeta != null) return apkMeta;

        apkMeta = ApkParsers.getMetaInfo(file);

        return apkMeta;
    }
}