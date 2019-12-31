package com.payneteasy.rtunnel.agent.api.messages.embed;

import lombok.Data;

@Data
public class AgentActionCloseTcpConnection {

    private final String sessionId;

}
