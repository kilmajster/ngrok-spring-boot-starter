package com.createam.ngrok.experimental.embedded;


import com.createam.ngrok.data.Tunnel;
import com.createam.ngrok.service.NgrokApiClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


public class NgrokEmbeddedRunner {

    private static final Logger log = LoggerFactory.getLogger(NgrokEmbeddedRunner.class);

    @Value("${server.port:8080}")
    private String port;

    @Autowired
    private NgrokConfiguration config;

    @Autowired
    private NgrokApiClient ngrokApiClient;

    @EventListener(ApplicationReadyEvent.class)
    public void startUpNgrok() {
        try {
            if (isCachedInstanceNotAvailable()) {
                log.info("Ngrok binary file not found in {}", buildBinaryFullPath(config.getCachePatch(), config.getBinaryName()));
                if (isZipFileNotAvailable()) {
                    log.info("Ngrok zip file not found!");
                    downloadTo(config.getCachePatch());
                }

                String zipFileFullName = buildBinaryFullPath(config.getCachePatch(), FilenameUtils.getName(config.getBinaryUrl()));
                ZipUtil.unzipArchiveTo(zipFileFullName, config.getCachePatch());
            }

            runNgrokProcess();

        } catch (Exception e) {
            log.error("Failed to run ngrok!", e);
        }
    }

    private void runNgrokProcess() throws IOException, InterruptedException {
        log.info("Starting ngrok process...");
        String ngrokBinaryFullPath = buildBinaryFullPath(config.getCachePatch(), config.getBinaryName());
        Runtime.getRuntime().exec(ngrokBinaryFullPath + " http " + port);
        Thread.sleep(3000);
        ngrokApiClient.fetchTunnels().forEach(this::logTunnelDetails);
    }

    private void logTunnelDetails(Tunnel tunnel) {
        log.info("Remote url ({}) -> \t{}", tunnel.getProto(), tunnel.getPublicUrl());
    }

    private boolean isZipFileNotAvailable() {
        String zipFileFullPath = buildBinaryFullPath(config.getCachePatch(), FilenameUtils.getName(config.getBinaryUrl()));

        return !Files.exists(Paths.get(zipFileFullPath));
    }

    private void downloadTo(String ngrokCachePath) throws IOException {
        String zipFileName = config.getBinaryUrl().substring(config.getBinaryUrl().lastIndexOf("/") + 1);
        String zipFileFullName = buildBinaryFullPath(ngrokCachePath, zipFileName);

        log.info("Downloading zip file from {} to {}", config.getBinaryUrl(), zipFileFullName);

        File targetFile = new File(zipFileFullName);
        long downloadStartTime = System.currentTimeMillis();
        FileUtils.copyURLToFile(new URL(config.getBinaryUrl()), targetFile);
        long downloadFinishTime = System.currentTimeMillis();

        log.info("Fetched {} kb in {} ms", FileUtils.sizeOf(targetFile) / 1024, downloadFinishTime - downloadStartTime);
    }


    private boolean isCachedInstanceNotAvailable() {
        String binaryFullName = buildBinaryFullPath(config.getCachePatch(), config.getBinaryName());
        return !Files.exists(Paths.get(binaryFullName));
    }

    private String buildBinaryFullPath(String cachePath, String binaryName) {
        return cachePath.endsWith(File.separator)
                ? cachePath.concat(binaryName)
                : cachePath.concat(File.separator).concat(binaryName);
    }


}
