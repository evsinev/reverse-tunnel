package com.payneteasy.rtunnel.server.tcp;

public interface ITcpSessionShadow {

    void tcpAddBytesToHttp(byte[] aBuffer);

    byte[] tcpPollBytes(int aSeconds);

    void httpAddBytesToTcp(byte[] aData);

    byte[] httpPollData(int aSeconds);

    String getSessionId();

}
