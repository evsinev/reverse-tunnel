package com.payneteasy.rtunnel.agent.api.messages;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentResponseIdentifier;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentTcpConnectionParameters;
import lombok.Builder;
import lombok.Data;

@Data
public class AgentTcpOpenConnectionResponse {

    private final AgentResponseIdentifier      responseIdentifier;
    private final boolean                      shouldConnect;
    private final AgentTcpConnectionParameters connectionParameters;

}
