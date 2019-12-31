package com.payneteasy.rtunnel.agent.api.messages;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentActionCloseTcpConnection;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentActionType;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentResponseIdentifier;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentActionResponse {

    private final AgentResponseIdentifier responseIdentifier;
    private final AgentActionType         type;

    private final AgentActionCloseTcpConnection actionCloseTcpConnection;
}
