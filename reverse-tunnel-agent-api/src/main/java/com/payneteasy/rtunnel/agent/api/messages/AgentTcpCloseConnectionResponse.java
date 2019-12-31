package com.payneteasy.rtunnel.agent.api.messages;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentResponseIdentifier;
import lombok.Data;

@Data
public class AgentTcpCloseConnectionResponse {

    private final AgentResponseIdentifier responseIdentifier;
    private final String                  sessionId;
    private final boolean                 shouldClose;
}
