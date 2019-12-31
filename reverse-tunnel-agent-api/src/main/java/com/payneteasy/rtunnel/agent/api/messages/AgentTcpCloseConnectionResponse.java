package com.payneteasy.rtunnel.agent.api.messages;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentResponseIdentifier;
import lombok.Data;

@Data
public class AgentTcpOpenConnectionResponse {

    private final AgentResponseIdentifier responseIdentifier;
    private final String                  sessionId;
    private final boolean                 shouldConnect;
    private final String                  address;
    private final int                     port;
    private final int                     connectionTimeoutMs;
    private final int                     readTimeoutMs;

}
