package com.payneteasy.rtunnel.server.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class TcpSocketInputTask implements Runnable {

    private final Logger        log;
    private final Socket        socket;
    private final ITcpShadow    shadow;
    private final SocketAddress remoteAddress;

    public TcpSocketInputTask(String sessionId, Socket socket, ITcpShadow aShadow) {
        this.socket = socket;
        this.shadow = aShadow;
        log = LoggerFactory.getLogger("tcp-socket-task-" + sessionId);
        remoteAddress = socket.getRemoteSocketAddress();
    }

    @Override
    public void run() {
        try {
            try (InputStream in = socket.getInputStream()) {
                byte[] buf = new byte[1024 * 20];
                int    count;
                while ((count = in.read(buf)) >= 0) {
                    shadow.onIncomingBytes(buf, 0, count);
                }
                closeSocket("Client closed socket");
            }
        } catch (IOException e) {
            log.error("{}: IO exception while processing socket", remoteAddress, e);
            closeSocket("IO error");
        } catch (Exception e) {
            log.error("{}: BAD response", remoteAddress, e);
            closeSocket("BAD response");
        }

    }

    private void closeSocket(String aReason) {
        try {
            log.debug("{}: Closing socket, reason: {}", remoteAddress, aReason);
            socket.close();
        } catch (IOException e) {
            log.error("{}: Cannot close socket", remoteAddress, e);
        }
    }
}
