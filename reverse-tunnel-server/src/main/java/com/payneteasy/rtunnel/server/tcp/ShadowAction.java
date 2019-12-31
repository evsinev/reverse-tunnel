package com.payneteasy.rtunnel.server.tcp;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentActionCloseTcpConnection;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentActionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShadowAction {

    private final AgentActionType               type;
    private final AgentActionCloseTcpConnection actionCloseTcpConnection;

}
