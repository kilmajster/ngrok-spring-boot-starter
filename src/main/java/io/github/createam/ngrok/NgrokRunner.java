package io.github.createam.ngrok;

import io.github.createam.ngrok.data.Tunnel;
import io.github.createam.ngrok.exception.NgrokDownloadException;
import io.github.createam.ngrok.exception.CommandExecuteException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class NgrokRunner {

    private static final Logger log = LoggerFactory.getLogger(NgrokRunner.class);

    @Value("${server.port:8080}")
    private String port;

    @Value("${ngrok.directory:}")
    private String ngrokDirectory;

    @Autowired
    private NgrokApiClient ngrokApiClient;

    @Autowired
    private NgrokDownloader ngrokDownloader;

    @Autowired
    private SystemCommandExecutor systemCommandExecutor;

    @Qualifier("ngrokExecutor")
    @Autowired
    private TaskExecutor ngrokExecutor;

    @EventListener(ApplicationReadyEvent.class)
    public void run() throws NgrokDownloadException, CommandExecuteException {
        ngrokExecutor.execute(() -> {
            if (ngrokIsNotRunning()) {
                if (needToDownloadNgrok()) {
                    ngrokDownloader.downloadAndExtractNgrokTo(getNgrokDirectoryOrDefault());
                }

                startupNgrok();
            }

            logTunnelsDetails();
        });
    }

    private boolean ngrokIsNotRunning() {
        return !ngrokApiClient.isResponding();
    }

    private boolean needToDownloadNgrok() {
        return !Files.isExecutable(Paths.get(getNgrokExecutablePath()));
    }

    private void startupNgrok() {
        log.info("Starting ngrok...");

        String command = getNgrokExecutablePath() + " http " + port;

        systemCommandExecutor.execute(command);

        log.info("Ngrok is running.");
    }

    private void logTunnelsDetails() {
        List<Tunnel> tunnels = ngrokApiClient.fetchTunnels();

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