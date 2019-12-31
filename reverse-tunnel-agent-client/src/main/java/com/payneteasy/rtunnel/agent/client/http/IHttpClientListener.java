package com.payneteasy.rtunnel.agent.client.http;

public interface IHttpClientListener {
    void onConnecting();

    void onConnected(int responseCode);

    void onReadOutput();
}
