package com.payneteasy.rtunnel.server.tcp;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentTcpConnectionParameters;

public interface ITcpShadowService {

    ITcpSessionShadow findShadow(String sessionId);

    ShadowAction pollAction(int aTimeoutSeconds, String agentName);

    AgentTcpConnectionParameters agentExchangeConnection(String aAgentName, int aTimeoutSeconds);

    ITcpSessionShadow tcpExchangeConnection(String aAgentName);
}
