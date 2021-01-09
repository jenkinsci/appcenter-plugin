package io.jenkins.plugins.appcenter.util;

import hudson.FilePath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;

public class RemoteFileUtils implements Serializable {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final FilePath filePath;

    @Nullable
    private File file;

    @Inject
    RemoteFileUtils(@Nonnull final FilePath filePath) {
        this.filePath = filePath;
    }

    @Nonnull
    public File getRemoteFile(@Nonnull String pathToRemoteFile) {
        if (file == null) {
            file = new File(filePath.child(pathToRemoteFile).getRemote());
        }

        return file;
    }

    @Nonnull
    public String getFileName(@Nonnull String pathToRemoveFile) {
        return getRemoteFile(pathToRemoveFile).getName();
    }

    public long getFileSize(@Nonnull String pathToRemoveFile) {
        return getRemoteFile(pathToRemoveFile).length();
    }

    @Nonnull
    public String getContentType(@Nonnull String pathToApp) {
        if (pathToApp.endsWith(".apk") || pathToApp.endsWith(".aab")) return "application/vnd.android.package-archive";
        if (pathToApp.endsWith(".msi")) return "application/x-msi";
        if (pathToApp.endsWith(".plist")) return "application/xml";
        if (pathToApp.endsWith(".aetx")) return "application/c-x509-ca-cert";
        if (pathToApp.endsWith(".cer")) return "application/pkix-cert";
        if (pathToApp.endsWith("xap")) return "application/x-silverlight-app";
        if (pathToApp.endsWith(".appx")) return "application/x-appx";
        if (pathToApp.endsWith(".appxbundle")) return "application/x-appxbundle";
        if (pathToApp.endsWith(".appxupload") || pathToApp.endsWith(".appxsym")) return "application/x-appxupload";
        if (pathToApp.endsWith(".msix")) return "application/x-msix";
        if (pathToApp.endsWith(".msixbundle")) return "application/x-msixbundle";
        if (pathToApp.endsWith(".msixupload") || pathToApp.endsWith(".msixsym")) return "application/x-msixupload";

        // Otherwise
        return "application/octet-stream";
    }
}