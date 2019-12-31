package com.payneteasy.rtunnel.server.config;

import com.payneteasy.startup.parameters.AStartupParameter;

public interface IServerStartupConfig {

    @AStartupParameter(name = "WEB_SERVER_PORT", value = "8083")
    int webServerPort();

    @AStartupParameter(name = "WEB_SERVER_CONTEXT", value = "/reverse-tunnel")
    String webServerContext();

    @AStartupParameter(name = "REDIRECT_CONFIG", value = "localhost:1122 192.168.123.11:22")
    String redirectConfig_01();


}
