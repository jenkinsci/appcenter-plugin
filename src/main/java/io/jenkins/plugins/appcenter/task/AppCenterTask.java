package io.jenkins.plugins.appcenter.task;

import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.model.AppCenter;
import io.jenkins.plugins.appcenter.remote.AppCenterService;
import io.jenkins.plugins.appcenter.remote.UploadService;
import jenkins.security.MasterToSlaveCallable;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

abstract class AppCenterTask extends MasterToSlaveCallable<Boolean, AppCenterException> {

    final PrintStream logger;
    final AppCenterService appCenterService;
    final UploadService uploadService;

    AppCenterTask(final TaskListener taskListener, final AppCenter appCenter) {
        this.logger = taskListener.getLogger();

        final MoshiConverterFactory converterFactory = MoshiConverterFactory.create();

        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        final OkHttpClient baseOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        final OkHttpClient appCenterClient = baseOkHttpClient.newBuilder()
                .addInterceptor(chain -> {
                    final Request request = chain.request();

                    final Headers newHeaders = request.headers().newBuilder()
                            .add("Accept", "application/json")
                            .add("Content-Type", "application/json")
                            .add("X-API-Token", Secret.toString(appCenter.getApiToken()))
                            .build();

                    final Request newRequest = request.newBuilder()
                            .headers(newHeaders)
                            .build();

                    return chain.proceed(newRequest);
                })
                .build();

        final Retrofit appCenterRetrofit = new Retrofit.Builder()
                .baseUrl(appCenter.getBaseUrl())
                .client(appCenterClient)
                .addConverterFactory(converterFactory)
                .build();

        final Retrofit uploadRetrofit = new Retrofit.Builder()
                .baseUrl(appCenter.getBaseUrl())
                .client(baseOkHttpClient)
                .addConverterFactory(converterFactory)
                .build();

        appCenterService = appCenterRetrofit.create(AppCenterService.class);
        uploadService = uploadRetrofit.create(UploadService.class);
    }

    @Override
    public final Boolean call() throws AppCenterException {
        try {
            return execute();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new AppCenterException(e);
        }
    }

    protected abstract Boolean execute() throws IOException, InterruptedException, AppCenterException, ExecutionException;
}
