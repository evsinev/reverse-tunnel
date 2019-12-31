package com.payneteasy.rtunnel.agent.api.messages.embed;

import lombok.Data;

@Data
public class AgentTcpConnectionParameters {

    private final String  sessionId;
    private final String  address;
    private final int     port;
    private final int     connectionTimeoutMs;
    private final int     readTimeoutMs;

}
