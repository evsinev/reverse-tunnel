package com.payneteasy.rtunnel.server.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class TcpSocketInputTask implements Runnable {

    private final Logger            log;
    private final Socket            socket;
    private final ITcpSessionShadow shadow;
    private final SocketAddress     remoteAddress;

    public TcpSocketInputTask(Socket socket, ITcpSessionShadow aShadow) {
        this.socket = socket;
        this.shadow = aShadow;
        log = LoggerFactory.getLogger("tcp-socket-task-" + shadow.getSessionId());
        remoteAddress = socket.getRemoteSocketAddress();
    }

    @Override
    public void run() {
        try {
            try (InputStream in = socket.getInputStream()) {
                byte[] buf = new byte[1024 * 20];
                int    count;
                while ((count = in.read(buf)) >= 0) {
                    byte[] data = new byte[count];
                    System.arraycopy(buf, 0, data, 0, count);
                    shadow.tcpAddBytesToHttp(data);
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
