package com.payneteasy.rtunnel.agent.core;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentRequestIdentifier;

import java.util.concurrent.atomic.AtomicLong;

public class AgentSequenceGeneratorImpl implements IAgentSequenceGenerator {

    private final AtomicLong sequence = new AtomicLong(0);
    private final String     agentName;

    public AgentSequenceGeneratorImpl(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public long nextSequence() {
        return sequence.incrementAndGet();
    }

    @Override
    public AgentRequestIdentifier nextId() {
        return new AgentRequestIdentifier(agentName, nextSequence(), Thread.currentThread().getName());
    }
}
