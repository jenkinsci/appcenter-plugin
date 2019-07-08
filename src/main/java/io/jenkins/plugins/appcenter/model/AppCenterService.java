package io.jenkins.plugins.appcenter.model;

import io.jenkins.plugins.appcenter.model.remote.ReleaseDetailsUpdateRequest;
import io.jenkins.plugins.appcenter.model.remote.ReleaseDetailsUpdateResponse;
import io.jenkins.plugins.appcenter.model.remote.ReleaseUploadBeginRequest;
import io.jenkins.plugins.appcenter.model.remote.ReleaseUploadBeginResponse;
import io.jenkins.plugins.appcenter.model.remote.ReleaseUploadEndRequest;
import io.jenkins.plugins.appcenter.model.remote.ReleaseUploadEndResponse;
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

    @POST("v0.1/apps/{owner_name}/{app_name}/release_uploads")
    CompletableFuture<ReleaseUploadBeginResponse> releaseUploadBegin(
            @Path("owner_name") String user,
            @Path("app_name") String appName,
            @Body ReleaseUploadBeginRequest releaseUploadBeginRequest);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/release_uploads/{upload_id}")
    CompletableFuture<ReleaseUploadEndResponse> releaseUploadEnd(
            @Path("owner_name") String user,
            @Path("app_name") String appName,
            @Path("upload_id") String uploadId,
            @Body ReleaseUploadEndRequest releaseUploadEndRequest);

    @PATCH("v0.1/apps/{owner_name}/{app_name}/releases/{release_id}")
    CompletableFuture<ReleaseDetailsUpdateResponse> releaseDetailsUpdate(
            @Path("owner_name") String user,
            @Path("app_name") String appName,
            @Path("release_id") int releaseId,
            @Body ReleaseDetailsUpdateRequest releaseDetailsUpdateRequest);
}
