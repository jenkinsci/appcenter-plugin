package io.jenkins.plugins.appcenter.api;

import hudson.ProxyConfiguration;
import hudson.util.Secret;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import static java.net.Proxy.Type.HTTP;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public final class AppCenterServiceFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String APPCENTER_BASE_URL = "https://api.appcenter.ms/";

    private final Secret apiToken;
    private final String baseUrl;

    private final ProxyConfiguration proxyConfiguration;

    public AppCenterServiceFactory(@Nonnull Secret apiToken, @Nullable URL baseUrl, @Nullable ProxyConfiguration proxyConfiguration) {
        this.apiToken = apiToken;
        this.baseUrl = baseUrl != null ? baseUrl.toString() : APPCENTER_BASE_URL;
        this.proxyConfiguration = proxyConfiguration;
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

        if (proxyConfiguration != null) {
            setProxy(proxyConfiguration, builder);
        }

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

        if (proxyConfiguration != null) {
            setProxy(proxyConfiguration, builder);
        }

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

    private void setProxy(
        final ProxyConfiguration proxyConfiguration,
        final OkHttpClient.Builder builder) {

        builder.proxy(new Proxy(HTTP, new InetSocketAddress(proxyConfiguration.name, proxyConfiguration.port)));

        final String username = proxyConfiguration.getUserName();
        final String password = proxyConfiguration.getPassword();

        if (isNotBlank(username) && isNotBlank(password)) {
            final String credentials = Credentials.basic(username, password);

            final Authenticator proxyAuthenticator = (route, response) -> response
                .request()
                .newBuilder()
                .header("Proxy-Authorization", credentials)
                .build();

            builder.proxyAuthenticator(proxyAuthenticator);
        }
    }
}