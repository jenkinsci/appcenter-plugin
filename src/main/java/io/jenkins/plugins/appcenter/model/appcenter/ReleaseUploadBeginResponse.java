package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class ReleaseUploadBeginResponse {
    @Nonnull
    public final String upload_id;
    @Nonnull
    public final String upload_url;
    @Nullable
    public final String asset_id;
    @Nullable
    public final String asset_domain;
    @Nullable
    public final String asset_token;

    public ReleaseUploadBeginResponse(@Nonnull String uploadId, @Nonnull String uploadUrl, @Nullable String assetId, @Nullable String assetDomain, @Nullable String assetToken) {
        this.upload_id = uploadId;
        this.upload_url = uploadUrl;
        this.asset_id = assetId;
        this.asset_domain = assetDomain;
        this.asset_token = assetToken;
    }

    @Override
    public String toString() {
        return "ReleaseUploadBeginResponse{" +
            "upload_id='" + upload_id + '\'' +
            ", upload_url='" + upload_url + '\'' +
            ", asset_id='" + asset_id + '\'' +
            ", asset_domain='" + asset_domain + '\'' +
            ", asset_token='" + asset_token + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseUploadBeginResponse that = (ReleaseUploadBeginResponse) o;
        return upload_id.equals(that.upload_id) &&
            upload_url.equals(that.upload_url) &&
            Objects.equals(asset_id, that.asset_id) &&
            Objects.equals(asset_domain, that.asset_domain) &&
            Objects.equals(asset_token, that.asset_token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upload_id, upload_url, asset_id, asset_domain, asset_token);
    }
}