package io.jenkins.plugins.appcenter.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.*;

import java.util.concurrent.CompletableFuture;

public interface UploadService {

    @Multipart
    @POST
    CompletableFuture<Void> uploadApp(@Url String url, @Part MultipartBody.Part file);

    @PUT
    CompletableFuture<Void> uploadSymbols(@Url String url, @Header("x-ms-blob-type") String msBlobTypeHeader, @Body RequestBody file);
}