package com.payneteasy.rtunnel.agent.client.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientListenerNoOp implements IHttpClientListener {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientListenerNoOp.class);

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
