package com.payneteasy.rtunnel.main;

import com.payneteasy.startup.parameters.AStartupParameter;

public interface IAgentStartupConfig {

    @AStartupParameter(name = "SERVER_BASE_URL", value = "http://localhost:9096/reverse-tunnel")
    String serverBaseUrl();

    @AStartupParameter(name = "AGENT_NAME", value = "agent-01")
    String agentName();


}
