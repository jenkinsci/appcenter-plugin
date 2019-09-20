package io.jenkins.plugins.appcenter.api;

import com.google.common.net.HttpHeaders;
import hudson.ProxyConfiguration;
import hudson.util.Secret;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.Serializable;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Singleton
public final class AppCenterServiceFactory implements Serializable {

    private static final String APPCENTER_BASE_URL = "https://api.appcenter.ms/";

    private static final long serialVersionUID = 1L;
    private static final int timeoutSeconds = 60;

    @Nonnull
    private final Secret apiToken;
    @Nonnull
    private final String baseUrl;
    @Nullable
    private final ProxyConfiguration proxyConfiguration;

    @Inject
    public AppCenterServiceFactory(@Nonnull Secret apiToken, @Nullable @Named("baseUrl") String baseUrl, @Nullable ProxyConfiguration proxyConfiguration) {
        this.apiToken = apiToken;
        this.baseUrl = baseUrl != null ? baseUrl : APPCENTER_BASE_URL;
        this.proxyConfiguration = proxyConfiguration;
    }

    public AppCenterService createAppCenterService() {
        final HttpUrl baseHttpUrl = HttpUrl.get(baseUrl);

        final OkHttpClient.Builder builder = createHttpClientBuilder(baseHttpUrl)
            .addInterceptor(chain -> {
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

        final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseHttpUrl)
            .client(builder.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build();

        return retrofit.create(AppCenterService.class);
    }

    public UploadService createUploadService(@Nonnull final String uploadUrl) {
        final HttpUrl httpUploadUrl = HttpUrl.get(uploadUrl);
        final HttpUrl baseUrl = HttpUrl.get(String.format("%1$s://%2$s/", httpUploadUrl.scheme(), httpUploadUrl.host()));

        final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createHttpClientBuilder(baseUrl).build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build();

        return retrofit.create(UploadService.class);
    }

    private OkHttpClient.Builder createHttpClientBuilder(@Nonnull final HttpUrl httpUrl) {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        return new OkHttpClient.Builder()
            .addInterceptor(logging)
            .proxy(setProxy(proxyConfiguration, httpUrl.host()))
            .proxyAuthenticator(setProxyAuthenticator(proxyConfiguration))
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS);
    }

    private Proxy setProxy(@Nullable final ProxyConfiguration proxyConfiguration,
                           @Nonnull final String host) {
        if (proxyConfiguration != null) {
            return proxyConfiguration.createProxy(host);
        } else {
            return Proxy.NO_PROXY;
        }
    }

    private Authenticator setProxyAuthenticator(@Nullable final ProxyConfiguration proxyConfiguration) {
        if (proxyConfiguration != null) {
            final String username = proxyConfiguration.getUserName();
            final String password = proxyConfiguration.getPassword();

            if (isNotBlank(username) && isNotBlank(password)) {
                final String credentials = Credentials.basic(username, password);

                return (route, response) -> response
                    .request()
                    .newBuilder()
                    .header(HttpHeaders.PROXY_AUTHORIZATION, credentials)
                    .build();
            }

            return Authenticator.NONE;
        }

        return Authenticator.NONE;
    }
}