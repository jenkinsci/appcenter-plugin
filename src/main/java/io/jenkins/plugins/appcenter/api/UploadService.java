package io.jenkins.plugins.appcenter.api;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

import java.util.concurrent.CompletableFuture;

public interface UploadService {

    @Multipart
    @POST
    CompletableFuture<Void> uploadApp(@Url String url, @Part MultipartBody.Part file);
}
