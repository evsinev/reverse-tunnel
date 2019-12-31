package com.payneteasy.rtunnel.server.service;

import com.payneteasy.rtunnel.agent.api.IReverseTunnelAgentService;
import com.payneteasy.rtunnel.agent.api.messages.*;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentActionType;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentResponseIdentifier;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentTcpConnectionParameters;
import com.payneteasy.rtunnel.server.tcp.ITcpSessionShadow;
import com.payneteasy.rtunnel.server.tcp.ITcpShadowService;
import com.payneteasy.rtunnel.server.tcp.ShadowAction;

import java.io.IOException;

public class ReverseTunnelAgentServiceImpl implements IReverseTunnelAgentService {

    private final ITcpShadowService shadowService;

    public ReverseTunnelAgentServiceImpl(ITcpShadowService shadowService) {
        this.shadowService = shadowService;
    }

    @Override
    public AgentActionResponse pollAction(AgentActionRequest aRequest) throws IOException {
        AgentResponseIdentifier responseId = aRequest.getRequestId().createResponseId();
        ShadowAction            action     = shadowService.pollAction(55, aRequest.getRequestId().getAgentName());

        switch (action.getType()) {
            case NO_ACTION:
                return AgentActionResponse.builder()
                        .responseIdentifier(responseId)
                        .type(AgentActionType.NO_ACTION)
                        .build();

            case CLOSE_TCP_CONNECTION:
                return AgentActionResponse.builder()
                        .responseIdentifier(responseId)
                        .type(AgentActionType.CLOSE_TCP_CONNECTION)
                        .actionCloseTcpConnection(action.getActionCloseTcpConnection())
                        .build();

            default:
                throw new IllegalStateException("Unknown action " + action.getType());
        }
    }

    @Override
    public AgentTcpOpenConnectionResponse pollOpenConnection(AgentTcpOpenConnectionRequest aRequest) throws IOException {
        AgentTcpConnectionParameters connectionParameters = shadowService.agentExchangeConnection(aRequest.getRequestIdentifier().getAgentName(), 55);
        if (connectionParameters == null) {
            return new AgentTcpOpenConnectionResponse(aRequest.getRequestIdentifier().createResponseId(), false, null);
        } else {
            return new AgentTcpOpenConnectionResponse(aRequest.getRequestIdentifier().createResponseId(), true, connectionParameters);
        }
    }

    @Override
    public AgentTcpPushDataResponse pushTcpData(AgentTcpPushDataRequest aRequest) throws IOException {
        ITcpSessionShadow shadow = shadowService.findShadow(aRequest.getSessionId());
        shadow.httpAddBytesToTcp(aRequest.getData());
        return new AgentTcpPushDataResponse(new AgentResponseIdentifier(aRequest.getRequestIdentifier()), aRequest.getSessionId());
    }

    @Override
    public AgentTcpPollDataResponse pollTcpData(AgentTcpPollDataRequest aRequest) throws IOException {
        ITcpSessionShadow shadow = shadowService.findShadow(aRequest.getSessionId());
        byte[]            bytes  = shadow.httpPollData(55);
        return new AgentTcpPollDataResponse(new AgentResponseIdentifier(aRequest.getRequestIdentifier()), aRequest.getSessionId(), bytes);
    }

}
