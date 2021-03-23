package io.jenkins.plugins.appcenter.util;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import javax.annotation.Nonnull;

import static java.net.HttpURLConnection.*;

public class MockWebServerUtil {

    public static void enqueueSuccess(final @Nonnull MockWebServer mockWebServer) {
        enqueueSuccess(mockWebServer, mockWebServer);
    }

    public static void enqueueUploadViaProxy(final @Nonnull MockWebServer mockWebServer, final @Nonnull MockWebServer proxyWebServer) {
        enqueueSuccess(mockWebServer, proxyWebServer);
    }

    public static void enqueueAppCenterViaProxy(final @Nonnull MockWebServer mockWebServer, final @Nonnull MockWebServer proxyWebServer) {
        enqueueSuccess(proxyWebServer, mockWebServer);
    }

    private static void enqueueSuccess(final @Nonnull MockWebServer mockAppCenterServer, final @Nonnull MockWebServer mockUploadServer) {
        // Create upload resource for app
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_CREATED).setBody("{\n" +
            "  \"id\": \"string\",\n" +
            "  \"upload_domain\": \"" + mockUploadServer.url("/").toString() + "\",\n" +
            "  \"asset_domain\": \"string\",\n" +
            "  \"url_encoded_token\": \"string\",\n" +
            "  \"package_asset_id\": \"string\"\n" +
            "}"));

        // Set Metadata
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"chunk_size\": 1234\n" +
            "}"));

        // Upload app
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK));

        // Finish Release
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK));

        // Update Release
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"id\": \"1234\",\n" +
            "  \"upload_status\": \"uploadFinished\"\n" +
            "}"));

        // Poll For Release
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"id\": \"1234\",\n" +
            "  \"upload_status\": \"readyToBePublished\",\n" +
            "  \"release_distinct_id\": \"4321\",\n" +
            "  \"release_url\": \"string\"\n" +
            "}"));

        // Distribute Resource
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"release_notes\": \"string\"\n" +
            "}"));
    }

    public static void enqueueSuccessWithSymbols(final @Nonnull MockWebServer mockAppCenterServer) {
        // Create upload resource for app
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_CREATED).setBody("{\n" +
            "  \"id\": \"string\",\n" +
            "  \"upload_domain\": \"" + mockAppCenterServer.url("/").toString() + "\",\n" +
            "  \"asset_domain\": \"string\",\n" +
            "  \"url_encoded_token\": \"string\",\n" +
            "  \"package_asset_id\": \"string\"\n" +
            "}"));

        // Create upload resource for debug symbols
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_CREATED).setBody("{\n" +
            "  \"symbol_upload_id\": \"string\",\n" +
            "  \"upload_url\": \"" + mockAppCenterServer.url("/").toString() + "\",\n" +
            "  \"expiration_date\": \"2020-03-18T21:16:22.188Z\"\n" +
            "}"));

        // Set Metadata
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"chunk_size\": 1234\n" +
            "}"));

        // Upload app
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK));

        // Upload debug symbols
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK));

        // Finish Release
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK));

        // Finish symbol release
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"symbol_upload_id\": \"string\",\n" +
            "  \"app_id\": \"string\",\n" +
            "  \"user\": {\n" +
            "    \"email\": \"string\",\n" +
            "    \"display_name\": \"string\"\n" +
            "  },\n" +
            "  \"status\": \"created\",\n" +
            "  \"symbol_type\": \"AndroidProguard\",\n" +
            "  \"symbols_uploaded\": [\n" +
            "    {\n" +
            "      \"symbol_id\": \"string\",\n" +
            "      \"platform\": \"string\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"origin\": \"User\",\n" +
            "  \"file_name\": \"string\",\n" +
            "  \"file_size\": 0,\n" +
            "  \"timestamp\": \"2019-11-17T12:12:06.701Z\"\n" +
            "}"));

        // Update Release
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"id\": \"1234\",\n" +
            "  \"upload_status\": \"uploadFinished\"\n" +
            "}"));

        // Poll For Release
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"id\": \"1234\",\n" +
            "  \"upload_status\": \"readyToBePublished\",\n" +
            "  \"release_distinct_id\": \"4321\",\n" +
            "  \"release_url\": \"string\"\n" +
            "}"));

        // Distribute app
        mockAppCenterServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"release_notes\": \"string\"\n" +
            "}"));
    }

    public static void enqueueFailure(final @Nonnull MockWebServer mockWebServer) {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HTTP_INTERNAL_ERROR));
    }

    public static void enqueueProxyAuthRequired(final @Nonnull MockWebServer proxyWebServer) {
        proxyWebServer.enqueue(new MockResponse().setResponseCode(HTTP_PROXY_AUTH));
    }
}