package com.payneteasy.rtunnel.server.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class TcpSocketOutputTask implements Runnable {

    private final Logger            log;
    private final Socket            socket;
    private final ITcpSessionShadow shadow;
    private final SocketAddress     remoteAddress;

    public TcpSocketOutputTask(Socket socket, ITcpSessionShadow aShadow) {
        this.socket = socket;
        this.shadow = aShadow;
        log = LoggerFactory.getLogger("tcp-socket-task-" + aShadow.getSessionId());
        remoteAddress = socket.getRemoteSocketAddress();
    }

    @Override
    public void run() {
        try {
            try (OutputStream out = socket.getOutputStream()) {
                while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                    byte[] buffer = shadow.tcpPollBytes(55);
                    if (buffer == null) {
                        continue;
                    }
                    out.write(buffer);
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
