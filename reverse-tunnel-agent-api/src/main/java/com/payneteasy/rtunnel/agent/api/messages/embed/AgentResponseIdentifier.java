package com.payneteasy.rtunnel.agent.api.messages.embed;

import lombok.Data;

@Data
public class AgentResponseIdentifier {

    private final String          agentName;
    private final long            sequenceNumber;

    public AgentResponseIdentifier(AgentRequestIdentifier aId) {
        agentName = aId.getAgentName();
        sequenceNumber = aId.getSequenceNumber();
    }
}
