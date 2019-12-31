package com.payneteasy.rtunnel.agent.api.messages.embed;

import lombok.Data;

@Data
public class AgentActionTcpOpenConnection {

    private final String  sessionId;
    private final String  hostname;
    private final int     port;
    private final int     connectionTimeoutMs;
    private final int     readTimeoutMs;

}
