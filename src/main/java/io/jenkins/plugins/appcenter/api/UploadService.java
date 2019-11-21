package io.jenkins.plugins.appcenter.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Url;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public interface UploadService {

    @Multipart
    @POST
    CompletableFuture<Void> uploadApp(@Url @Nonnull String url, @Part @Nonnull MultipartBody.Part file);

    @Headers("x-ms-blob-type: BlockBlob")
    @PUT
    CompletableFuture<Void> uploadSymbols(@Url @Nonnull String url, @Body @Nonnull RequestBody file);

}