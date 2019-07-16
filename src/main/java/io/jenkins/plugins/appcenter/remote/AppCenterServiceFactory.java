package io.jenkins.plugins.appcenter.remote;

import hudson.util.Secret;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.net.URL;

public class AppCenterServiceFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String APPCENTER_BASE_URL = "https://api.appcenter.ms/";

    private final Secret apiToken;
    private final String ownerName;
    private final String appName;
    private final String distributionGroup;
    private final String pathToApp;
    private final String baseUrl;

    public AppCenterServiceFactory(@Nonnull Secret apiToken, @Nonnull String ownerName, @Nonnull String appName,
                                   @Nonnull String distributionGroup, @Nonnull String pathToApp, @Nullable URL baseUrl) {
        this.apiToken = apiToken;
        this.ownerName = ownerName;
        this.appName = appName;
        this.distributionGroup = distributionGroup;
        this.pathToApp = pathToApp;
        this.baseUrl = baseUrl != null ? baseUrl.toString() : APPCENTER_BASE_URL;
    }

    public AppCenterService createAppCenterService() {
        final MoshiConverterFactory converterFactory = MoshiConverterFactory.create();

        final OkHttpClient.Builder builder = createHttpClientBuilder();

        builder.addInterceptor(chain -> {
            final Request request = chain.request();

            final Headers newHeaders = request.headers().newBuilder()
                    .add("Accept", "application/json")
                    .add("Content-Type", "application/json")
                    .add("X-API-Token", Secret.toString(apiToken))
                    .build();

            final Request newRequest = request.newBuilder()
                    .headers(newHeaders)
                    .build();

            return chain.proceed(newRequest);
        });

        final OkHttpClient okHttpClient = builder.build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(converterFactory)
                .build();

        return retrofit.create(AppCenterService.class);
    }

    public UploadService createUploadService() {
        final MoshiConverterFactory converterFactory = MoshiConverterFactory.create();

        final OkHttpClient.Builder builder = createHttpClientBuilder();

        final OkHttpClient okHttpClient = builder.build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(converterFactory)
                .build();

        return retrofit.create(UploadService.class);
    }

    private OkHttpClient.Builder createHttpClientBuilder() {

        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        return new OkHttpClient.Builder()
                .addInterceptor(logging);
    }

    public Secret getApiToken() {
        return apiToken;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getAppName() {
        return appName;
    }

    public String getDistributionGroup() {
        return distributionGroup;
    }

    public String getPathToApp() {
        return pathToApp;
    }
}
