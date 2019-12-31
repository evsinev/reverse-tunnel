package com.payneteasy.rtunnel.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.rtunnel.agent.api.IReverseTunnelAgentService;
import com.payneteasy.rtunnel.agent.client.ReverseTunnelAgentServiceClient;
import com.payneteasy.rtunnel.agent.client.http.HttpClientListenerNoOp;
import com.payneteasy.rtunnel.agent.client.http.HttpClientParameters;
import com.payneteasy.rtunnel.agent.core.AgentPollActionTask;
import com.payneteasy.rtunnel.agent.core.AgentPollOpenTcpConnectionTask;
import com.payneteasy.rtunnel.agent.core.AgentSequenceGeneratorImpl;
import com.payneteasy.rtunnel.agent.core.util.HexTypeAdapter;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class TunnelAgentApplication {

    public static void main(String[] args) {


        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(byte[].class, new HexTypeAdapter())
                .setPrettyPrinting()
                .create();

        IAgentStartupConfig        startupConfig  = getStartupParameters(IAgentStartupConfig.class);
        HttpClientParameters       httpParameters = new HttpClientParameters(30_000, 60_000, gson, Collections.emptyMap());
        IReverseTunnelAgentService agentClient    = new ReverseTunnelAgentServiceClient(httpParameters, startupConfig.serverBaseUrl(), new HttpClientListenerNoOp());

        Executor                       executor              = Executors.newFixedThreadPool(1000);
        String                         agentName             = startupConfig.agentName();
        AgentSequenceGeneratorImpl     sequenceGenerator     = new AgentSequenceGeneratorImpl(agentName);
        AgentPollOpenTcpConnectionTask openTcpConnectionTask = new AgentPollOpenTcpConnectionTask(agentClient, sequenceGenerator, agentName, executor);
        AgentPollActionTask            pollActionTask        = new AgentPollActionTask(agentClient, sequenceGenerator, agentName);

        Thread connectionThread = new Thread(openTcpConnectionTask);
        Runtime.getRuntime().addShutdownHook(new Thread(connectionThread::interrupt));
        connectionThread.start();

        pollActionTask.run();

    }
}
