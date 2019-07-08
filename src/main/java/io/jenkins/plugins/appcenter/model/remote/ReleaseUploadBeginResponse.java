package io.jenkins.plugins.appcenter.model.remote;

public final class ReleaseUploadBeginResponse {
    public final String upload_id;
    public final String upload_url;
    public final String asset_id;
    public final String asset_domain;
    public final String asset_token;

    ReleaseUploadBeginResponse(String uploadId, String uploadUrl, String assetId, String assetDomain, String assetToken) {
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
}