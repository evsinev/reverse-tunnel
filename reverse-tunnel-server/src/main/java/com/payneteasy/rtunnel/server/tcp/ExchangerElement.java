package com.payneteasy.rtunnel.server.tcp;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentTcpConnectionParameters;
import lombok.Data;

@Data
public class ExchangerElement {

    private final AgentTcpConnectionParameters connectionParameters;
}
