package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ReleaseUploadBeginResponse {
    @Nonnull
    public final String id;
    @Nonnull
    public final String upload_domain;
    @Nonnull
    public final String token;
    @Nonnull
    public final String url_encoded_token;
    @Nonnull
    public final String package_asset_id;

    public ReleaseUploadBeginResponse(@Nonnull String id, @Nonnull String uploadDomain, @Nonnull String token, @Nonnull String urlEncodedToken, @Nonnull String packageAssetId) {
        this.id = id;
        this.upload_domain = uploadDomain;
        this.token = token;
        this.url_encoded_token = urlEncodedToken;
        this.package_asset_id = packageAssetId;
    }

    @Override
    public String toString() {
        return "ReleaseUploadBeginResponse{" +
            "id='" + id + '\'' +
            ", upload_domain='" + upload_domain + '\'' +
            ", token='" + token + '\'' +
            ", url_encoded_token='" + url_encoded_token + '\'' +
            ", package_asset_id='" + package_asset_id + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseUploadBeginResponse that = (ReleaseUploadBeginResponse) o;
        return id.equals(that.id) &&
            upload_domain.equals(that.upload_domain) &&
            token.equals(that.token) &&
            url_encoded_token.equals(that.url_encoded_token) &&
            package_asset_id.equals(that.package_asset_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upload_domain, token, url_encoded_token, package_asset_id);
    }
}