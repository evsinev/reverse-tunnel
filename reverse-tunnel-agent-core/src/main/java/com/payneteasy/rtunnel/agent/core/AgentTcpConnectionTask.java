package com.payneteasy.rtunnel.agent.core;

import com.payneteasy.rtunnel.agent.api.IReverseTunnelAgentService;
import com.payneteasy.rtunnel.agent.api.messages.AgentTcpPollDataRequest;
import com.payneteasy.rtunnel.agent.api.messages.AgentTcpPollDataResponse;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentTcpConnectionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executor;

public class AgentTcpConnectionTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AgentTcpConnectionTask.class);

    private final IReverseTunnelAgentService   agentClient;
    private final IAgentSequenceGenerator      sequenceGenerator;
    private final String                       sessionId;
    private final Executor                     executor;
    private final AgentTcpConnectionParameters parameters;

    public AgentTcpConnectionTask(IReverseTunnelAgentService agentClient, IAgentSequenceGenerator sequenceGenerator, String aSessionId, Executor executor, AgentTcpConnectionParameters parameters) {
        this.agentClient = agentClient;
        this.sequenceGenerator = sequenceGenerator;
        this.sessionId = aSessionId;
        this.executor = executor;
        this.parameters = parameters;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(parameters.getReadTimeoutMs());
            LOG.debug("Connecting to {} ...", parameters);
            socket.connect(new InetSocketAddress(parameters.getAddress(), parameters.getPort()), parameters.getConnectionTimeoutMs());

            LOG.debug("Connected to {}", parameters);
            executor.execute(new AgentTcpSocketReadTask(socket, agentClient, sequenceGenerator, sessionId));

            OutputStream out = socket.getOutputStream();
            while(!Thread.currentThread().isInterrupted()) {
                AgentTcpPollDataResponse response = agentClient.pollTcpData(new AgentTcpPollDataRequest(sequenceGenerator.nextId(), sessionId));
                out.write(response.getData());
            }
        } catch (Exception e) {
            LOG.error("Cannot connect to {}", parameters, e);
        } finally {
            closeSocket(socket);
        }



    }
    private void closeSocket(Socket aSocket) {
        try {
            aSocket.close();
        } catch (IOException e) {
            LOG.error("Cannot close socket", e);
        }
    }

}
