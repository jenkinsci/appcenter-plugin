package io.jenkins.plugins.appcenter.api;

import com.google.common.net.HttpHeaders;
import hudson.ProxyConfiguration;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterException;
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
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public final class AppCenterServiceFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String APPCENTER_BASE_URL = "https://api.appcenter.ms/";

    private final Secret apiToken;
    private final String baseUrl;
    private final ProxyConfiguration proxyConfiguration;

    public AppCenterServiceFactory(@Nonnull Secret apiToken, @Nullable URL baseUrl, @Nullable ProxyConfiguration proxyConfiguration) {
        this.apiToken = apiToken;
        this.baseUrl = baseUrl != null ? baseUrl.toString() : APPCENTER_BASE_URL;
        this.proxyConfiguration = proxyConfiguration;
    }

    public AppCenterService createAppCenterService() throws AppCenterException {
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
            String appCenterHost;
            try {
                appCenterHost = new URL(APPCENTER_BASE_URL).getHost();
            } catch (MalformedURLException e) {
                throw new AppCenterException(e);
            }

            setProxy(proxyConfiguration, appCenterHost, builder);
        }

        final OkHttpClient okHttpClient = builder.build();

        final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build();

        return retrofit.create(AppCenterService.class);
    }

    public UploadService createUploadService(String host) {
        final MoshiConverterFactory converterFactory = MoshiConverterFactory.create();

        final OkHttpClient.Builder builder = createHttpClientBuilder();

        if (proxyConfiguration != null) {
            setProxy(proxyConfiguration, host, builder);
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
        final String host,
        final OkHttpClient.Builder builder) {

        builder.proxy(proxyConfiguration.createProxy(host));

        final String username = proxyConfiguration.getUserName();
        final String password = proxyConfiguration.getPassword();

        if (isNotBlank(username) && isNotBlank(password)) {
            final String credentials = Credentials.basic(username, password);

            final Authenticator proxyAuthenticator = (route, response) -> response
                .request()
                .newBuilder()
                .header(HttpHeaders.PROXY_AUTHORIZATION, credentials)
                .build();

            builder.proxyAuthenticator(proxyAuthenticator);
        }
    }
}