package com.createam.ngrok.service;

import com.createam.ngrok.data.Tunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.util.List;

public class NgrokRunner {
    private static final Logger log = LoggerFactory.getLogger(NgrokRunner.class);

    @Value("${server.port:8080}")
    public String port;

    @Autowired
    private NgrokApiClient ngrokApiClient;

    private String ngrokPath;

    public NgrokRunner(String ngrokPath) {
        this.ngrokPath = ngrokPath;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startUpNgrok() throws InterruptedException {
        List<Tunnel> tunnels = ngrokApiClient.fetchTunnels();

        while (tunnels.isEmpty()) {
            runNgrokProcess();
            Thread.sleep(3000);
            tunnels = ngrokApiClient.fetchTunnels();
        }

        tunnels.forEach(this::logTunnelDetails);
    }

    private void logTunnelDetails(Tunnel tunnel) {
        log.info("Remote url ({}) -> {}", tunnel.getProto(), tunnel.getPublicUrl());
    }


    public void runNgrokProcess() {
        log.info("Starting ngrok process...");
        try {
            Runtime.getRuntime().exec(ngrokPath + " http " + port);
        } catch (IOException e) {
            log.error("Failed to run ngrok!", e);
        }
    }
}
