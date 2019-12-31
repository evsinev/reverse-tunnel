package com.payneteasy.rtunnel.server.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class TcpServer {

    private static final Logger LOG = LoggerFactory.getLogger(TcpServer.class);

    private final ServerSocket    serverSocket;
    private final ExecutorService executor;
    private final int             readTimeoutMs;
    private final ITcpShadowService      shadowService;


    public TcpServer(
            String            aListenAddress
            , int             aPort
            , ExecutorService executor
            , int             readTimeoutMs
            , ITcpShadowService aShadowService
    ) throws IOException {
        this.executor = executor;
        this.readTimeoutMs = readTimeoutMs;
        shadowService = aShadowService;
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(aListenAddress, aPort));
    }

    public void acceptSocketAndWait() {
        Thread.currentThread().setName("tcp-accept");
        LOG.info("Start listening  {}...", serverSocket);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                LOG.info("Got connection from {}", socket.getRemoteSocketAddress());
                socket.setSoTimeout(readTimeoutMs);

                ITcpSessionShadow shadow = shadowService.tcpExchangeConnection("agent-01");

                LOG.debug("Shadow is {}", shadow);
                if(shadow == null) {
                    socket.close();
                } else {
                    executor.execute(new TcpSocketInputTask (socket, shadow));
                    executor.execute(new TcpSocketOutputTask(socket, shadow));
                }

            } catch (SocketException e) {
                if("Socket closed".equals(e.getMessage())) {
                    LOG.warn("Socket closed");
                    break;
                } else {
                    LOG.error("Cannot deal with socket", e);
                }
            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted()) {
                    LOG.error("Exiting from the listening server socket ...");
                    break;
                }
                LOG.error("Cannot deal with socket", e);
            }
        }
    }

//    private String createSessionId() {
//        return UUID.randomUUID().toString();
//    }

    public void closeServer() {

        try {
            LOG.info("Waiting for current tasks ...");
            executor.shutdown();
            executor.shutdownNow();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            LOG.warn("Cannot stop tasks", e);
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            LOG.warn("Cannot close server socket", e);
        }
    }

}
