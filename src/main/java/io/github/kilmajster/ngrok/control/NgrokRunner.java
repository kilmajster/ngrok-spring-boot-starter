package io.github.kilmajster.ngrok.control;

import io.github.kilmajster.ngrok.data.NgrokTunnel;
import io.github.kilmajster.ngrok.exception.NgrokCommandExecuteException;
import io.github.kilmajster.ngrok.exception.NgrokDownloadException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class NgrokRunner {

    private static final Logger log = LoggerFactory.getLogger(NgrokRunner.class);

    private final String port;
    private final String ngrokDirectory;
    private final String ngrokConfigFilePath;
    private final NgrokApiClient ngrokApiClient;
    private final NgrokDownloader ngrokDownloader;
    private final NgrokSystemCommandExecutor systemCommandExecutor;
    private final TaskExecutor ngrokExecutor;

    public NgrokRunner(String port, String ngrokDirectory, String ngrokConfigFilePath, NgrokApiClient ngrokApiClient,
            NgrokDownloader ngrokDownloader, NgrokSystemCommandExecutor systemCommandExecutor, TaskExecutor ngrokExecutor) {
        this.port = port;
        this.ngrokDirectory = ngrokDirectory;
        this.ngrokConfigFilePath = ngrokConfigFilePath;
        this.ngrokApiClient = ngrokApiClient;
        this.ngrokDownloader = ngrokDownloader;
        this.systemCommandExecutor = systemCommandExecutor;
        this.ngrokExecutor = ngrokExecutor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() throws NgrokDownloadException, NgrokCommandExecuteException {
        ngrokExecutor.execute(() -> {

            if (ngrokIsNotRunning()) {

                if (needToDownloadNgrok()) {
                    ngrokDownloader.downloadAndExtractNgrokTo(getNgrokDirectoryOrDefault());

                    addPermissionsIfNeeded();
                }

                startupNgrok();
            }

            logTunnelsDetails();
        });
    }

    private void addPermissionsIfNeeded() {
        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
            String chmod = "chmod +x ".concat(getNgrokExecutablePath());

            log.info("Running: " + chmod);

            systemCommandExecutor.execute(chmod);
        }
    }

    private boolean ngrokIsNotRunning() {
        return !ngrokApiClient.isResponding();
    }

    private boolean needToDownloadNgrok() {
        return !Files.isExecutable(Paths.get(getNgrokExecutablePath()));
    }

    private void startupNgrok() {

        String command = getNgrokExecutablePath()
                + " http "
                + prepareNgrokConfigParams(ngrokConfigFilePath)
                + port;

        log.info("Starting ngrok with command = {}", command);

        systemCommandExecutor.execute(command);

        log.info("Ngrok is running. Dashboard url -> {}", ngrokApiClient.getNgrokApiUrl());
    }

    private String prepareNgrokConfigParams(String ngrokConfigFilePath) {
        return StringUtils.isBlank(ngrokConfigFilePath)
                ? "" // no config arguments
                : StringUtils.split(ngrokConfigFilePath, ";").length == 1 ? "-config " + ngrokConfigFilePath + " " // 1 config arg
                : "-config " + String.join(" -config ", StringUtils.split(ngrokConfigFilePath, ";")) + " "; // multiple configs
    }

    private void logTunnelsDetails() {
        List<NgrokTunnel> tunnels = ngrokApiClient.fetchTunnels();

        tunnels.forEach(t -> log.info("Remote url ({}) -> {}", t.getProto(), t.getPublicUrl()));
    }

    private String getNgrokExecutablePath() {
        String executable = SystemUtils.IS_OS_WINDOWS ? "ngrok.exe" : "ngrok";

        return getNgrokDirectoryOrDefault() + File.separator + executable;
    }

    private String getNgrokDirectoryOrDefault() {
        return StringUtils.isNotBlank(ngrokDirectory) ? ngrokDirectory : getDefaultNgrokDirectory();
    }

    private String getDefaultNgrokDirectory() {
        return FilenameUtils.concat(FileUtils.getUserDirectory().getPath(), ".ngrok2");
    }
}