package io.jenkins.plugins.appcenter.api;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import javax.annotation.Nonnull;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

public class MockWebServerUtil {

    public static void success(final @Nonnull MockWebServer mockWebServer) {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HTTP_CREATED).setBody("{\n" +
            "  \"upload_id\": \"string\",\n" +
            "  \"upload_url\": \"" + mockWebServer.url("/").toString() + "\",\n" +
            "  \"asset_id\": \"string\",\n" +
            "  \"asset_domain\": \"string\",\n" +
            "  \"asset_token\": \"string\"\n" +
            "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HTTP_OK));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"release_id\": 0,\n" +
            "  \"release_url\": \"string\"\n" +
            "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HTTP_OK).setBody("{\n" +
            "  \"release_notes\": \"string\"\n" +
            "}"));
    }

    public static void failure(final @Nonnull MockWebServer mockWebServer) {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HTTP_INTERNAL_ERROR));
    }
}