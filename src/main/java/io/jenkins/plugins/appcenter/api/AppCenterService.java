package io.jenkins.plugins.appcenter.api;

import io.jenkins.plugins.appcenter.model.appcenter.ReleaseDetailsUpdateRequest;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseDetailsUpdateResponse;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadBeginRequest;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadBeginResponse;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadEndRequest;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadEndResponse;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadBeginRequest;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadBeginResponse;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadEndRequest;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadEndResponse;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.concurrent.CompletableFuture;

public interface AppCenterService {

    @POST("v0.1/apps/{owner_name}/{app_name}/release_uploads")
    CompletableFuture<ReleaseUploadBeginResponse> releaseUploadsCreate(
        @Path("owner_name") String user,
        @Path("app_name") String appName);

    @POST("v0.1/apps/{owner_name}/{app_name}/release_uploads")
    CompletableFuture<ReleaseUploadBeginResponse> releaseUploadsCreate(
        @Path("owner_name") String user,
        @Path("app_name") String appName,
        @Body ReleaseUploadBeginRequest releaseUploadBeginRequest);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/release_uploads/{upload_id}")
    CompletableFuture<ReleaseUploadEndResponse> releaseUploadsComplete(
        @Path("owner_name") String user,
        @Path("app_name") String appName,
        @Path("upload_id") String uploadId,
        @Body ReleaseUploadEndRequest releaseUploadEndRequest);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/releases/{release_id}")
    CompletableFuture<ReleaseDetailsUpdateResponse> releasesUpdate(
        @Path("owner_name") String user,
        @Path("app_name") String appName,
        @Path("release_id") int releaseId,
        @Body ReleaseDetailsUpdateRequest releaseDetailsUpdateRequest);

    @POST("v0.1/apps/{owner_name}/{app_name}/symbol_uploads")
    CompletableFuture<SymbolUploadBeginResponse> symbolUploadsCreate(
        @Path("owner_name") String user,
        @Path("app_name") String appName,
        @Body SymbolUploadBeginRequest symbolUploadBeginRequest);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/symbol_uploads/{symbol_upload_id}")
    CompletableFuture<SymbolUploadEndResponse> symbolUploadsComplete(
        @Path("owner_name") String user,
        @Path("app_name") String appName,
        @Path("symbol_upload_id") String uploadId,
        @Body SymbolUploadEndRequest symbolUploadEndRequest);
}