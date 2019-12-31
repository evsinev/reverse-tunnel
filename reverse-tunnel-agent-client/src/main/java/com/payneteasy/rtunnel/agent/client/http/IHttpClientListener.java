package com.payneteasy.reverseterminal.client.http;

public interface IHttpClientListener {
    void onConnecting();

    void onConnected(int responseCode);

    void onReadOutput();
}
