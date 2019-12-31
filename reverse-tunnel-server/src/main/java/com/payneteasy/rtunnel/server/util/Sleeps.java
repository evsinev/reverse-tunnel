package com.payneteasy.mastercard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Sleeps {

    private static final Logger LOG = LoggerFactory.getLogger(Sleeps.class);


    public static void sleepSeconds(int aSeconds) {
        try {
            TimeUnit.SECONDS.sleep(aSeconds);
        } catch (InterruptedException e) {
            LOG.warn("Interrupted", e);
            Thread.currentThread().interrupt();
        }

    }
}
