package com.payneteasy.rtunnel.agent.core;

import com.payneteasy.rtunnel.agent.api.IReverseTunnelAgentService;
import com.payneteasy.rtunnel.agent.api.messages.AgentTcpOpenConnectionRequest;
import com.payneteasy.rtunnel.agent.api.messages.AgentTcpOpenConnectionResponse;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentRequestIdentifier;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentTcpConnectionParameters;
import com.payneteasy.rtunnel.agent.core.util.Sleeps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executor;

public class AgentPollOpenTcpConnectionTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AgentPollOpenTcpConnectionTask.class);

    private final IReverseTunnelAgentService agentClient;
    private final IAgentSequenceGenerator    sequenceGenerator;
    private final String                     agentName;
    private final Executor                   executor;

    public AgentPollOpenTcpConnectionTask(IReverseTunnelAgentService agentClient, IAgentSequenceGenerator sequenceGenerator, String agentName, Executor aExecutor) {
        this.agentClient = agentClient;
        this.sequenceGenerator = sequenceGenerator;
        this.agentName = agentName;
        executor = aExecutor;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            AgentRequestIdentifier requestId = new AgentRequestIdentifier(agentName, sequenceGenerator.nextSequence(), Thread.currentThread().getName());
            AgentTcpOpenConnectionRequest request = new AgentTcpOpenConnectionRequest(requestId);
            try {
                AgentTcpOpenConnectionResponse response = agentClient.pollOpenConnection(request);
                if(!response.isShouldConnect()) {
                    LOG.info("Sleeping 5 seconds on error connection ...");
                    Sleeps.sleepSeconds(5);
                    continue;
                }
                openConnection(response.getConnectionParameters());
            } catch (IOException e) {
                LOG.error("IO Error connection", e);
                LOG.warn("Sleeping 5 seconds on IO error while connecting ...");
                Sleeps.sleepSeconds(5);
            } catch (Exception e) {
                LOG.error("Unknown error", e);
            } finally {
                Sleeps.sleepSeconds(1);
            }
        }
    }

    private void openConnection(AgentTcpConnectionParameters aParameters) {
        executor.execute(new AgentTcpConnectionTask(agentClient, sequenceGenerator, agentName, executor, aParameters));
    }
}
