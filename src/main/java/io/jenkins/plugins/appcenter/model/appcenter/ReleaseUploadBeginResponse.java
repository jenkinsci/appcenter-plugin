package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ReleaseUploadBeginResponse {
    public final String upload_id;
    public final String upload_url;
    public final String asset_id;
    public final String asset_domain;
    public final String asset_token;

    public ReleaseUploadBeginResponse(@Nonnull String uploadId, @Nonnull String uploadUrl, @Nonnull String assetId, @Nonnull String assetDomain, @Nonnull String assetToken) {
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
            asset_id.equals(that.asset_id) &&
            asset_domain.equals(that.asset_domain) &&
            asset_token.equals(that.asset_token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upload_id, upload_url, asset_id, asset_domain, asset_token);
    }
}