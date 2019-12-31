package com.payneteasy.rtunnel.agent.client.http;

import com.google.gson.Gson;

import java.util.Map;

public class HttpClientParameters {

    private final int                 connectionTimeoutMs;
    private final int                 readTimeoutMs;
    private final Gson                gson;
    private final Map<String, String> headers;

    public HttpClientParameters(int connectionTimeoutMs, int readTimeoutMs, Gson gson, Map<String, String> headers) {
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.readTimeoutMs       = readTimeoutMs;
        this.gson                = gson;
        this.headers             = headers;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public Gson getGson() {
        return gson;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
