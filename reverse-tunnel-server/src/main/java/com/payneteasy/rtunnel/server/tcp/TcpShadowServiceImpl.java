package com.payneteasy.rtunnel.server.tcp;

import com.payneteasy.rtunnel.agent.api.messages.embed.AgentActionType;
import com.payneteasy.rtunnel.agent.api.messages.embed.AgentTcpConnectionParameters;
import com.payneteasy.rtunnel.server.config.TcpPipeConfig;
import com.payneteasy.rtunnel.server.util.Sleeps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TcpShadowServiceImpl implements ITcpShadowService {

    private static final Logger LOG = LoggerFactory.getLogger(TcpShadowServiceImpl.class);

    private final Map<String, ITcpSessionShadow>           shadowMap    = new ConcurrentHashMap<>();
    private final Map<String, Exchanger<ExchangerElement>> exchangerMap = new ConcurrentHashMap<>();
    private final TcpPipeConfig pipeConfig;

    public TcpShadowServiceImpl(TcpPipeConfig pipeConfig) {
        this.pipeConfig = pipeConfig;
    }

    @Override
    public ITcpSessionShadow findShadow(String sessionId) {
        ITcpSessionShadow shadow = shadowMap.get(sessionId);
        if(shadow == null) {
            throw new IllegalStateException("No shadow for sessionId " + sessionId);
        }
        return shadow;
    }

    @Override
    public ShadowAction pollAction(int aTimeoutSeconds, String agentName) {
        Sleeps.sleepSeconds(55);
        return new ShadowAction(AgentActionType.NO_ACTION, null);
    }

    @Override
    public AgentTcpConnectionParameters agentExchangeConnection(String aAgentName, int aTimeoutSeconds) {
        Exchanger<ExchangerElement> exchanger = exchangerMap.computeIfAbsent(aAgentName, s -> new Exchanger<>());
        LOG.debug("Found exchanger {} for agent with agent name {}", exchanger, aAgentName);
        try {
            LOG.debug("Starting exchange from HTTP to TCP ...");
            ExchangerElement theirElement = exchanger.exchange(new ExchangerElement(null), aTimeoutSeconds, TimeUnit.SECONDS);
            return theirElement.getConnectionParameters();
        } catch (InterruptedException e) {
            LOG.error("Interrupted", e);
            Thread.currentThread().interrupt();
            return null;
        } catch (TimeoutException e) {
            LOG.debug("No tcp server exchange");
            return null;
        }
    }

    @Override
    public ITcpSessionShadow tcpExchangeConnection(String aAgentName) {
        Exchanger<ExchangerElement> exchanger = exchangerMap.computeIfAbsent(aAgentName, s -> new Exchanger<>());
        LOG.debug("Found exchanger {} for tcp server with agent name {}", exchanger, aAgentName);
        try {
            String sessionId = UUID.randomUUID().toString();
            AgentTcpConnectionParameters connectionParameters = new AgentTcpConnectionParameters(
                    sessionId
                    , pipeConfig.getTargetAddress()
                    , pipeConfig.getTargetPort()
                    , 10_000
                    , 120_000
            );
            LOG.debug("Starting exchange from TCP to HTTP ...");
            ExchangerElement theirElement = null;
            try {
                theirElement = exchanger.exchange(new ExchangerElement(connectionParameters), 55, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                LOG.warn("No http from other side", e);
                return null;
            }
            if (theirElement == null) {
                return null;
            }

            ITcpSessionShadow shadow = new TcpSessionShadowImpl(sessionId);
            shadowMap.put(aAgentName, shadow);
            return shadow;

        } catch (InterruptedException e) {
            LOG.error("Interrupted", e);
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
