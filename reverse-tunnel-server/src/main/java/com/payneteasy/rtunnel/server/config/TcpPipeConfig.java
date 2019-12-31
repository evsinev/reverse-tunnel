package com.payneteasy.rtunnel.server.config;

import lombok.Getter;

import java.util.StringTokenizer;

@Getter
public class TcpPipeConfig {

    private final String listenAddress;
    private final int    listenPort;
    private final String targetAddress;
    private final int    targetPort;

    public TcpPipeConfig(String aLine) {
        StringTokenizer st = new StringTokenizer(aLine, ": ");
        listenAddress = st.nextToken();
        listenPort = Integer.parseInt(st.nextToken());
        targetAddress = st.nextToken();
        targetPort = Integer.parseInt(st.nextToken());
    }

}
