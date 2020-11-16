package io.jenkins.plugins.appcenter.api;

import com.azure.core.http.ProxyOptions;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.google.common.net.HttpHeaders;
import hudson.ProxyConfiguration;
import hudson.util.Secret;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import static com.azure.core.http.ProxyOptions.Type.HTTP;
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

    public BlobClient createBlobUploadService(@Nonnull final String uploadUrl) {
        //Workaround for bug in Azure Blob Storage, as AppCenter returns the upload URL with a port attached
        //See https://github.com/Azure/azure-sdk-for-java/issues/15827
        final HttpUrl httpUploadUrl = HttpUrl.get(uploadUrl);
        final String file;
        if (httpUploadUrl.encodedQuery() != null) {
            file = httpUploadUrl.encodedPath() + "?" + httpUploadUrl.encodedQuery();
        } else {
            file = httpUploadUrl.encodedPath();
        }

        final String uploadUrlWithoutPort = String.format("%1$s://%2$s%3$s", httpUploadUrl.scheme(), httpUploadUrl.host(), file);

        // For the future it would be nice to simply configure this with the azure-core-http-okhttp artefact so that we can make use of the shared OkHttp configuration for
        // timeouts and proxy settings. For the time being configure this manually. https://github.com/Azure/azure-sdk-for-java/wiki/HTTP-clients


        final NettyAsyncHttpClientBuilder nettyAsyncHttpClientBuilder = new NettyAsyncHttpClientBuilder();

        if (proxyConfiguration != null) {
            // Note in the future we will configure an OkHttp client instead of a NettyHttp client. For the time being the function name setProxy does not make a lot of sense for
            // Netty use. We are merely using it to get a configuration from Jenkins in order t set Netty ProxyOptions if needed.
            final Proxy proxy = setProxy(proxyConfiguration, httpUploadUrl.host());
            final ProxyOptions proxyOptions;
            if (proxy != Proxy.NO_PROXY) {
                // If this destination should go via a proxy configure it here.
                proxyOptions = new ProxyOptions(HTTP, new InetSocketAddress(httpUploadUrl.host(), httpUploadUrl.port()));
            } else {
                // Otherwise it should go direct.
                proxyOptions = null;
            }

            final String username = proxyConfiguration.getUserName();
            final String password = proxyConfiguration.getPassword();

            if (proxyOptions != null && isNotBlank(username) && isNotBlank(password)) {
                // Additionally if we have a valid username:password set in Jenkins Global configuration for proxies then make use of it.
                proxyOptions.setCredentials(username, password);
            }

            nettyAsyncHttpClientBuilder
                .proxy(proxyOptions);
        }

        return new BlobClientBuilder()
            .endpoint(uploadUrlWithoutPort)
            .httpClient(nettyAsyncHttpClientBuilder.build())
            .buildClient();
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