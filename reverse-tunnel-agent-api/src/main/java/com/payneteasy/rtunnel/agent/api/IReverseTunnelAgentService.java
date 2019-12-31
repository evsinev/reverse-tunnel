package com.payneteasy.rtunnel.agent.api;

import com.payneteasy.rtunnel.agent.api.messages.*;

import java.io.IOException;

public interface IReverseTunnelAgentService {

    AgentActionResponse pollAction(AgentActionRequest aRequest) throws IOException;

    AgentTcpOpenConnectionResponse pollOpenConnection(AgentTcpOpenConnectionRequest aRequest) throws IOException;

    AgentTcpPushDataResponse pushTcpData(AgentTcpPushDataRequest aRequest) throws IOException;

    AgentTcpPollDataResponse pollTcpData(AgentTcpPollDataRequest aRequest) throws IOException;

}
