package com.payneteasy.rtunnel.server.tcp;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentActionType;
import lombok.Data;

@Data
public class TcpShadowAction {

    private final AgentActionType type;

}
