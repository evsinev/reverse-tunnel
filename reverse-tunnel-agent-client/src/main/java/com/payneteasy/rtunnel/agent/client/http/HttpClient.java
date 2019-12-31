package com.payneteasy.reverseterminal.client.http;

import com.google.gson.Gson;
import com.payneteasy.android.sdk.logger.ILogger;
import com.payneteasy.android.sdk.logger.SdkLoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class HttpClient implements Closeable {

    private final static ILogger LOG = SdkLoggerFactory.getLogger(HttpClient.class);

    private final String              uploadUrl;
    private final HttpURLConnection   connection;
    private final Gson                gson;
    private final IHttpClientListener listener;

    public HttpClient(String aUploadUrl, HttpClientParameters aParams) throws IOException {
        this(aUploadUrl, aParams, new HttpClientListenerNoOp());

    }
    public HttpClient(String aUploadUrl, HttpClientParameters aParams, IHttpClientListener aListener) throws IOException {
        this(aUploadUrl, aParams.getConnectionTimeoutMs(), aParams.getReadTimeoutMs(), aParams.getHeaders(), aParams.getGson(), aListener);
    }

    public HttpClient(String aUploadUrl, int aConnectionTimeoutMs, int aReadTimeoutMs, Map<String, String> aHeaders, Gson aGson, IHttpClientListener aListener) throws IOException {
        gson = aGson;
        listener = aListener;
        HttpURLConnection.setFollowRedirects(false);

        uploadUrl  = aUploadUrl;
        connection = (HttpURLConnection) new URL(aUploadUrl).openConnection();
        connection.setReadTimeout(aReadTimeoutMs);
        connection.setConnectTimeout(aConnectionTimeoutMs);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/octet-stream");

        LOG.debug("Sending headers {} ...", aHeaders);
        for (Map.Entry<String, String> entry : aHeaders.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        connection.setDoOutput(true);

        LOG.debug("Connecting to {} ...", uploadUrl);

    }

    public OutputStream getOutputStream() throws IOException {
        return connection.getOutputStream();
    }

    public <R, T> T postJson(R aRequest, Class<T> aResponseClass) throws IOException {
        LOG.debug("Trying to convert to json for upload url {}: {}", uploadUrl, aRequest);
        String jsonRequest = gson.toJson(aRequest);
        LOG.debug("Sending json ...\n{}", jsonRequest);
        getOutputStream().write(jsonRequest.getBytes(StandardCharsets.UTF_8));

        listener.onConnecting();

        int         responseCode = waitForResponseCode();
        listener.onConnected(responseCode);

        InputStream inputStream  = connection.getInputStream();
        T           response     = gson.fromJson(new InputStreamReader(inputStream), aResponseClass);

        listener.onReadOutput();

        LOG.debug("Response code is {}", responseCode);
        return response;
    }

    public void close() throws IOException {
        closeConnection();
    }

    private void closeConnection() throws IOException {
        LOG.debug("{}. Closing connection to {}", connection.getResponseMessage(), uploadUrl);
        connection.disconnect();
    }

    private int waitForResponseCode() throws IOException {
        LOG.debug("Waiting for response code ...");
        int responseCode = connection.getResponseCode();
        if (responseCode != HTTP_OK) {
            throw new IOException("Response code is " + responseCode + " for url " + uploadUrl);
        }
        return responseCode;
    }
}
