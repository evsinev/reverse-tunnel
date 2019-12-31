package com.payneteasy.rtunnel.agent.core;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentRequestIdentifier;

public interface IAgentSequenceGenerator {

    long nextSequence();

    AgentRequestIdentifier nextId();
}
