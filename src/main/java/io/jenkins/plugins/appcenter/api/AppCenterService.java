package io.jenkins.plugins.appcenter.api;

import io.jenkins.plugins.appcenter.model.appcenter.*;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.concurrent.CompletableFuture;

public interface AppCenterService {

    @POST("v0.1/apps/{owner_name}/{app_name}/release_uploads")
    CompletableFuture<ReleaseUploadBeginResponse> releaseUploadBegin(
        @Path("owner_name") String user,
        @Path("app_name") String appName);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/release_uploads/{upload_id}")
    CompletableFuture<ReleaseUploadEndResponse> releaseUploadEnd(
        @Path("owner_name") String user,
        @Path("app_name") String appName,
        @Path("upload_id") String uploadId,
        @Body UploadEndRequest releaseUploadEndRequest);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/releases/{release_id}")
    CompletableFuture<ReleaseDetailsUpdateResponse> releaseDetailsUpdate(
        @Path("owner_name") String user,
        @Path("app_name") String appName,
        @Path("release_id") int releaseId,
        @Body ReleaseDetailsUpdateRequest releaseDetailsUpdateRequest);

    @POST("v0.1/apps/{owner_name}/{app_name}/symbol_uploads")
    CompletableFuture<SymbolsUploadBeginResponse> symbolsUploadBegin(
        @Path("owner_name") String user,
        @Path("app_name") String appName,
        @Body SymbolsUploadBeginRequest symbolsUploadBeginRequest);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/symbol_uploads/{symbol_upload_id}")
    CompletableFuture<SymbolsUploadEndResponse> symbolsUploadEnd(
        @Path("owner_name") String user,
        @Path("app_name") String appName,
        @Path("symbol_upload_id") String uploadId,
        @Body UploadEndRequest releaseUploadEndRequest);
}