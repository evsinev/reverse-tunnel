package com.payneteasy.rtunnel.agent.client;

import com.payneteasy.rtunnel.agent.api.IReverseTunnelAgentService;
import com.payneteasy.rtunnel.agent.api.messages.*;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentRequestIdentifier;
import com.payneteasy.rtunnel.agent.client.http.HttpClient;
import com.payneteasy.rtunnel.agent.client.http.HttpClientParameters;
import com.payneteasy.rtunnel.agent.client.http.IHttpClientListener;

import java.io.IOException;

public class ReverseTunnelAgentServiceClient implements IReverseTunnelAgentService {

    private final String               baseUrl;
    private final HttpClientParameters parameters;
    private final IHttpClientListener  listener;

    public ReverseTunnelAgentServiceClient(HttpClientParameters aParameters, String aBaseUrl, IHttpClientListener aListener) {
        baseUrl = aBaseUrl + "/agent";
        parameters = aParameters;
        listener = aListener;
    }

    @Override
    public AgentActionResponse pollAction(AgentActionRequest aRequest) throws IOException {
        return sendRequest("poll-action", aRequest.getRequestId(), aRequest, AgentActionResponse.class);
    }

    @Override
    public AgentTcpOpenConnectionResponse pollOpenConnection(AgentTcpOpenConnectionRequest aRequest) throws IOException {
        return sendRequest("poll-open-tcp", aRequest.getRequestIdentifier(), aRequest, AgentTcpOpenConnectionResponse.class);
    }

    @Override
    public AgentTcpPushDataResponse pushTcpData(AgentTcpPushDataRequest aRequest) throws IOException {
        return sendRequest("push-tcp-data", aRequest.getRequestIdentifier(), aRequest, AgentTcpPushDataResponse.class);
    }

    @Override
    public AgentTcpPollDataResponse pollTcpData(AgentTcpPollDataRequest aRequest) throws IOException {
        return sendRequest("poll-tcp-data", aRequest.getRequestIdentifier(), aRequest, AgentTcpPollDataResponse.class);
    }

    private <R, T> T sendRequest(String aPath, AgentRequestIdentifier identifier, R aRequest, Class<T> aResponseClass) throws IOException {
        String url = String.format("%s/%s/%s/%d", baseUrl, aPath, identifier.getAgentName(), identifier.getSequenceNumber());

        HttpClient client = new HttpClient(url, parameters, listener);
        //noinspection TryFinallyCanBeTryWithResources
        try {
            return client.postJson(aRequest, aResponseClass);
        } finally {
            client.close();
        }
    }

}
