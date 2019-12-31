package com.payneteasy.rtunnel.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.apiservlet.GsonJettyContextHandler;
import com.payneteasy.rtunnel.agent.api.IReverseTunnelAgentService;
import com.payneteasy.rtunnel.agent.api.messages.*;
import com.payneteasy.rtunnel.server.config.IServerStartupConfig;
import com.payneteasy.rtunnel.server.config.TcpPipeConfig;
import com.payneteasy.rtunnel.server.service.ReverseTunnelAgentServiceImpl;
import com.payneteasy.rtunnel.server.tcp.ITcpShadowService;
import com.payneteasy.rtunnel.server.tcp.TcpServer;
import com.payneteasy.rtunnel.server.tcp.TcpShadowServiceImpl;
import com.payneteasy.rtunnel.server.util.HexTypeAdapter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class TunnelServerApplication {

    public static void main(String[] args) throws Exception {
        IServerStartupConfig startupConfig = getStartupParameters(IServerStartupConfig.class);

        TcpPipeConfig pipeConfig = new TcpPipeConfig(startupConfig.redirectConfig_01());

        ITcpShadowService shadowService = new TcpShadowServiceImpl(pipeConfig);

        createTcpServer(pipeConfig, shadowService);

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(byte[].class, new HexTypeAdapter())
                .setPrettyPrinting()
                .create();

        Server                  server      = new Server(startupConfig.webServerPort());
        ServletContextHandler   context     = new ServletContextHandler(server, startupConfig.webServerContext(), ServletContextHandler.NO_SESSIONS);
        GsonJettyContextHandler gsonHandler = new GsonJettyContextHandler(context, gson);

        IReverseTunnelAgentService agentService = new ReverseTunnelAgentServiceImpl(shadowService);
        gsonHandler.addApi("/agent/poll-action/*"   , agentService::pollAction , AgentActionRequest.class     );
        gsonHandler.addApi("/agent/poll-open-tcp/*" , agentService::pollOpenConnection , AgentTcpOpenConnectionRequest.class     );
        gsonHandler.addApi("/agent/poll-tcp-data/*" , agentService::pollTcpData, AgentTcpPollDataRequest.class);
        gsonHandler.addApi("/agent/push-tcp-data/*" , agentService::pushTcpData, AgentTcpPushDataRequest.class);

        server.setStopAtShutdown(true);

        server.start();
    }

    private static void createTcpServer(TcpPipeConfig pipeConfig, ITcpShadowService shadowService) throws IOException {
        ExecutorService executor        = Executors.newFixedThreadPool(1000);
        TcpServer       tcpServer       = new TcpServer(pipeConfig.getListenAddress(), pipeConfig.getListenPort(), executor, 35_000, shadowService);
        Thread          tcpServerThread = new Thread(tcpServer::acceptSocketAndWait);

        Runtime.getRuntime().addShutdownHook(new Thread(tcpServer::closeServer));
        tcpServerThread.start();
    }
}
