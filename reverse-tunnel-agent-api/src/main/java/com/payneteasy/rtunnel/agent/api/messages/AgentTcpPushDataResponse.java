package com.payneteasy.rtunnel.agent.api.messages;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentResponseIdentifier;
import lombok.Data;

@Data
public class AgentTcpPushDataResponse {

    private final AgentResponseIdentifier responseIdentifier;
    private final String                  sessionId;
}
