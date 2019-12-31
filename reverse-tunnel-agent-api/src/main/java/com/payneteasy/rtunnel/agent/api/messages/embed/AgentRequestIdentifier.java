package com.payneteasy.rtunnel.agent.api.messages.embed;

import lombok.Data;

@Data
public class AgentRequestIdentifier {

    private final String agentName;
    private final long   sequenceNumber;
    private final String threadName;

    public AgentResponseIdentifier createResponseId() {
        return new AgentResponseIdentifier(this);
    }
}
