package com.payneteasy.rtunnel.agent.core;

import com.payneteasy.rtunnel.agent.api.IReverseTunnelAgentService;
import com.payneteasy.rtunnel.agent.api.messages.AgentTcpPushDataRequest;
import com.payneteasy.rtunnel.agent.api.messages.AgentTcpPushDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class AgentTcpSocketReadTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AgentTcpSocketReadTask.class);

    private final Socket                     socket;
    private final IReverseTunnelAgentService agentClient;
    private final IAgentSequenceGenerator    generator;
    private final String                     sessionId;

    public AgentTcpSocketReadTask(Socket socket, IReverseTunnelAgentService agentClient, IAgentSequenceGenerator sequenceGenerator, String aSessionId) {
        this.socket = socket;
        this.agentClient = agentClient;
        this.generator = sequenceGenerator;
        sessionId = aSessionId;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                InputStream in  = socket.getInputStream();
                byte[]      buf = new byte[4096];
                int         count;
                while ((count = in.read(buf)) >= 0) {
                    byte[] data = new byte[count];
                    System.arraycopy(buf, 0, data, 0, count);
                    AgentTcpPushDataResponse response = agentClient.pushTcpData(new AgentTcpPushDataRequest(generator.nextId(), sessionId, data));
                }
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        } finally {
            closeSocket();
            closeHttpSession();
        }
    }

    private void closeHttpSession() {
//        agentClient.closeConnection();// todo close remote connection
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            LOG.error("Cannot close socket", e);
        }
    }
}
