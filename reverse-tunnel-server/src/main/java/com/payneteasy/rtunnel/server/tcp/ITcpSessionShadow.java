package com.payneteasy.rtunnel.server.tcp;

public interface ITcpShadow {

    void tcpAddBytesToHttp(byte[] aBuffer);

    byte[] tcpPollBytes(int aSeconds);


    void httpAddBytesToTcp(byte[] aData);

    byte[] httpPollData(int aSeconds);
}
