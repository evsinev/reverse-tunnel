package com.payneteasy.rtunnel.agent.api.messages;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentRequestIdentifier;
import lombok.Data;

@Data
public class AgentTcpCloseConnectionRequest {

    private final AgentRequestIdentifier requestIdentifier;
    private final String                 sessionId;
}
