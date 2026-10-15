package com.payneteasy.rtunnel.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.apiservlet.GsonApiServlet;
import com.payneteasy.apiservlet.IApiCall;
import com.payneteasy.rtunnel.agent.api.IReverseTunnelAgentService;
import com.payneteasy.rtunnel.agent.api.messages.*;
import com.payneteasy.rtunnel.server.config.IServerStartupConfig;
import com.payneteasy.rtunnel.server.config.TcpPipeConfig;
import com.payneteasy.rtunnel.server.service.ReverseTunnelAgentServiceImpl;
import com.payneteasy.rtunnel.server.tcp.ITcpShadowService;
import com.payneteasy.rtunnel.server.tcp.TcpServer;
import com.payneteasy.rtunnel.server.tcp.TcpShadowServiceImpl;
import com.payneteasy.rtunnel.server.util.HexTypeAdapter;
import org.eclipse.jetty.ee8.servlet.ServletContextHandler;
import org.eclipse.jetty.ee8.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class TunnelServerApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TunnelServerApplication.class);

    public static void main(String[] args) throws Exception {
        IServerStartupConfig startupConfig = getStartupParameters(IServerStartupConfig.class);

        TcpPipeConfig pipeConfig = new TcpPipeConfig(startupConfig.redirectConfig_01());

        ITcpShadowService shadowService = new TcpShadowServiceImpl(pipeConfig);

        createTcpServer(pipeConfig, shadowService);

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(byte[].class, new HexTypeAdapter())
                .setPrettyPrinting()
                .create();

        Server                server  = new Server(startupConfig.webServerPort());
        ServletContextHandler context = new ServletContextHandler(server, startupConfig.webServerContext(), ServletContextHandler.NO_SESSIONS);

        IReverseTunnelAgentService agentService = new ReverseTunnelAgentServiceImpl(shadowService);
        addApi(context, gson, "/agent/poll-action/*"   , agentService::pollAction , AgentActionRequest.class     );
        addApi(context, gson, "/agent/poll-open-tcp/*" , agentService::pollOpenConnection , AgentTcpOpenConnectionRequest.class     );
        addApi(context, gson, "/agent/poll-tcp-data/*" , agentService::pollTcpData, AgentTcpPollDataRequest.class);
        addApi(context, gson, "/agent/push-tcp-data/*" , agentService::pushTcpData, AgentTcpPushDataRequest.class);

        server.setStopAtShutdown(true);

        server.start();
    }

    /**
     * Same as api-servlet GsonJettyContextHandler.addApi(), which is compiled against Jetty 9 and cannot be used with Jetty 12
     */
    private static <I, O> void addApi(ServletContextHandler aContext, Gson aGson, String aPath, IApiCall<I, O> aApiCall, Class<I> aRequestClass) {
        LOG.info("Adding servlet {}", aPath);
        aContext.addServlet(new ServletHolder(new GsonApiServlet<>(aApiCall, aRequestClass, aGson)), aPath);
    }

    private static void createTcpServer(TcpPipeConfig pipeConfig, ITcpShadowService shadowService) throws IOException {
        ExecutorService executor        = Executors.newFixedThreadPool(1000);
        TcpServer       tcpServer       = new TcpServer(pipeConfig.getListenAddress(), pipeConfig.getListenPort(), executor, 35_000, shadowService);
        Thread          tcpServerThread = new Thread(tcpServer::acceptSocketAndWait);

        Runtime.getRuntime().addShutdownHook(new Thread(tcpServer::closeServer));
        tcpServerThread.start();
    }
}
