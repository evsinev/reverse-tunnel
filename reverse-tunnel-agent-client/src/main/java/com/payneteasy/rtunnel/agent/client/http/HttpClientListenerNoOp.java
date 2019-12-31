package com.payneteasy.reverseterminal.client.http;

import com.payneteasy.android.sdk.logger.ILogger;
import com.payneteasy.android.sdk.logger.SdkLoggerFactory;

public class HttpClientListenerNoOp implements IHttpClientListener {

    private final static ILogger LOG = SdkLoggerFactory.getLogger(HttpClient.class);

    @Override
    public void onConnecting() {
        LOG.debug("onConnecting()");
    }

    @Override
    public void onConnected(int responseCode) {
        LOG.debug("onConnected({})", responseCode);

    }

    @Override
    public void onReadOutput() {
        LOG.debug("onReadOutput()");

    }
}
