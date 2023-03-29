package com.payneteasy.rtunnel.agent.core;

import com.payneteasy.rtunnel.agent.api.IReverseTunnelAgentService;
import com.payneteasy.rtunnel.agent.api.messages.AgentActionRequest;
import com.payneteasy.rtunnel.agent.api.messages.AgentActionResponse;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentRequestIdentifier;
import com.payneteasy.rtunnel.agent.core.util.Sleeps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AgentPollActionTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AgentPollActionTask.class);

    private final IReverseTunnelAgentService agentClient;
    private final IAgentSequenceGenerator sequenceGenerator;
    private final String agentName;

    public AgentPollActionTask(IReverseTunnelAgentService agentClient, IAgentSequenceGenerator sequenceGenerator, String agentName) {
        this.agentClient = agentClient;
        this.sequenceGenerator = sequenceGenerator;
        this.agentName = agentName;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            AgentRequestIdentifier requestId = new AgentRequestIdentifier(agentName, sequenceGenerator.nextSequence(), Thread.currentThread().getName());
            AgentActionRequest     request   = new AgentActionRequest(requestId);
            try {
                AgentActionResponse    response = agentClient.pollAction(request);
                processResponse(response);
            } catch (IOException e) {
                LOG.error("IO Error polling", e);
                LOG.warn("Sleeping 5 seconds on IO error while polling ...");
                Sleeps.sleepSeconds(5);
            } catch (Exception e) {
                LOG.error("Unknown error", e);
            } finally {
                LOG.info("Sleeping 1 seconds between polling ...");
                Sleeps.sleepSeconds(1);
            }
        }
    }

    private void processResponse(AgentActionResponse aResponse) {
        switch (aResponse.getType()) {
            case NO_ACTION:
                return;

            case CLOSE_TCP_CONNECTION:
                LOG.warn("// todo implement close tcp connection");
                return;

            default:
                throw new IllegalStateException("Unknown type " + aResponse.getType());
        }
    }
}
