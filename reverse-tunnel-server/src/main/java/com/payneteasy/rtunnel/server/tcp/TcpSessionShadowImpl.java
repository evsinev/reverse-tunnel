package com.payneteasy.rtunnel.server.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TcpSessionShadowImpl implements ITcpSessionShadow {

    private static final Logger LOG = LoggerFactory.getLogger(TcpSessionShadowImpl.class);

    private final ArrayBlockingQueue<byte[]> tcpToHttpQueue = new ArrayBlockingQueue<>(1024);
    private final ArrayBlockingQueue<byte[]> httpToTcpQueue = new ArrayBlockingQueue<>(1024);
    private final String                     sessionId;

    public TcpSessionShadowImpl(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void tcpAddBytesToHttp(byte[] aBuffer) {
        tcpToHttpQueue.add(aBuffer);
    }

    @Override
    public void httpAddBytesToTcp(byte[] aData) {
        httpToTcpQueue.add(aData);
    }

    @Override
    public byte[] tcpPollBytes(int aSeconds) {
        try {
            return httpToTcpQueue.poll(aSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("Interrupted tcpPollBytes()", e);
            Thread.currentThread().interrupt();
            return new byte[0];
        }
    }


    @Override
    public byte[] httpPollData(int aSeconds) {
        try {
            return tcpToHttpQueue.poll(aSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("Interrupted httpPollData()", e);
            Thread.currentThread().interrupt();
            return new byte[0];
        }
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return "TcpSessionShadowImpl{" +
                "sessionId='" + sessionId + '\'' +
                '}';
    }
}
