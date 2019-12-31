package com.payneteasy.rtunnel.agent.api.messages;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentTcpConnectionParameters;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentRequestIdentifier;
import lombok.Data;

@Data
public class AgentTcpOpenConnectionRequest {

    private final AgentRequestIdentifier       requestIdentifier;
}
