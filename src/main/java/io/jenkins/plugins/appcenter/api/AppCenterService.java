package io.jenkins.plugins.appcenter.api;

import io.jenkins.plugins.appcenter.model.appcenter.*;
import retrofit2.http.*;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public interface AppCenterService {

    @POST("v0.1/apps/{owner_name}/{app_name}/uploads/releases")
    CompletableFuture<ReleaseUploadBeginResponse> releaseUploadsCreate(
        @Path("owner_name") @Nonnull String user,
        @Path("app_name") @Nonnull String appName,
        @Body @Nonnull ReleaseUploadBeginRequest releaseUploadBeginRequest);

    @POST
    CompletableFuture<SetMetadataResponse> setMetaData(@Url @Nonnull String url);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/release_uploads/{upload_id}")
    CompletableFuture<ReleaseUploadEndResponse> releaseUploadsComplete(
        @Path("owner_name") @Nonnull String user,
        @Path("app_name") @Nonnull String appName,
        @Path("upload_id") @Nonnull String uploadId,
        @Body @Nonnull ReleaseUploadEndRequest releaseUploadEndRequest);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/releases/{release_id}")
    CompletableFuture<ReleaseDetailsUpdateResponse> releasesUpdate(
        @Path("owner_name") @Nonnull String user,
        @Path("app_name") @Nonnull String appName,
        @Path("release_id") @Nonnull Integer releaseId,
        @Body @Nonnull ReleaseUpdateRequest releaseUpdateRequest);

    @POST("v0.1/apps/{owner_name}/{app_name}/symbol_uploads")
    CompletableFuture<SymbolUploadBeginResponse> symbolUploadsCreate(
        @Path("owner_name") @Nonnull String user,
        @Path("app_name") @Nonnull String appName,
        @Body @Nonnull SymbolUploadBeginRequest symbolUploadBeginRequest);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/symbol_uploads/{symbol_upload_id}")
    CompletableFuture<SymbolUpload> symbolUploadsComplete(
        @Path("owner_name") @Nonnull String user,
        @Path("app_name") @Nonnull String appName,
        @Path("symbol_upload_id") @Nonnull String uploadId,
        @Body @Nonnull SymbolUploadEndRequest symbolUploadEndRequest);
}