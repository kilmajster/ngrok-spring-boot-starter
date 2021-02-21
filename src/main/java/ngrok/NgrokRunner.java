package ngrok;

import ngrok.api.NgrokApiClient;
import ngrok.api.model.NgrokTunnel;
import ngrok.exception.NgrokCommandExecuteException;
import ngrok.exception.NgrokDownloadException;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import ngrok.os.NgrokSystemCommandExecutor;
import ngrok.util.NgrokDownloader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;

import java.util.List;

/**
 * For details see <a href="https://github.com/kilmajster/ngrok-spring-boot-starter">docs</a>.
 */
public class NgrokRunner {

    private static final Logger log = LoggerFactory.getLogger(NgrokRunner.class);

    @Value("${" + NgrokProperties.SPRING_SERVER_PORT + ":" + NgrokProperties.SPRING_SERVER_PORT_DEFAULT + "}")
    private String springServerPort;

    @Value("${" + NgrokProperties.NGROK_CONFIG + ":}")
    private String ngrokConfigFilePath;

    @Value("${" + NgrokProperties.NGROK_COMMAND + ":}")
    private String ngrokCustomCommand;

    private final NgrokApiClient ngrokApiClient;
    private final NgrokBinaryProvider ngrokBinaryProvider;
    private final NgrokDownloader ngrokDownloader;
    private final NgrokPlatformDetector ngrokPlatformDetector;
    private final NgrokSystemCommandExecutor ngrokSystemCommandExecutor;
    private final TaskExecutor ngrokExecutor;

    public NgrokRunner(
            NgrokApiClient ngrokApiClient, NgrokBinaryProvider ngrokBinaryProvider, NgrokDownloader ngrokDownloader,
            NgrokPlatformDetector ngrokPlatformDetector, NgrokSystemCommandExecutor ngrokSystemCommandExecutor, TaskExecutor ngrokExecutor) {
        this.ngrokApiClient = ngrokApiClient;
        this.ngrokBinaryProvider = ngrokBinaryProvider;
        this.ngrokDownloader = ngrokDownloader;
        this.ngrokPlatformDetector = ngrokPlatformDetector;
        this.ngrokSystemCommandExecutor = ngrokSystemCommandExecutor;
        this.ngrokExecutor = ngrokExecutor;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void run() throws NgrokDownloadException, NgrokCommandExecuteException {
        ngrokExecutor.execute(() -> {
            if (ngrokIsNotRunning()) {
                if (needToDownloadNgrok()) {
                    downloadAndExtractNgrokBinary();
                    addPermissionsIfNeeded();
                }
                startNgrok();
            } else {
                log.info("Ngrok was already running! Dashboard url -> [ {} ]", ngrokApiClient.getNgrokApiUrl());
            }
            logTunnelsDetails();
        });
    }

    private void downloadAndExtractNgrokBinary() {
        ngrokDownloader.downloadAndExtractNgrokTo(ngrokBinaryProvider.getNgrokDirectoryOrDefault());
    }

    private void addPermissionsIfNeeded() {
        if (ngrokPlatformDetector.isUnix()) {
            final String chmod = "chmod +x ".concat(ngrokBinaryProvider.getNgrokBinaryFilePath());

            log.info("Running: " + chmod);

            ngrokSystemCommandExecutor.execute(chmod);
        }
    }

    private boolean ngrokIsNotRunning() {
        return !ngrokApiClient.isResponding();
    }

    private boolean needToDownloadNgrok() {
        return !ngrokBinaryProvider.isNgrokBinaryPresent();
    }

    private void startNgrok() {
        String command = isCustomConfigPresent() ? buildCustomShellCmd() : buildNgrokDefaultShellCmd();
        log.debug("Starting ngrok with command = [ {} ]", command);

        ngrokSystemCommandExecutor.execute(command);

        if (ngrokApiClient.isResponding()) {
            log.info("Ngrok started successfully! Dashboard url -> [ {} ]", ngrokApiClient.getNgrokApiUrl());
        } else {
            log.warn("Ngrok seems to not responding! Ngrok status url = [ {} ] Ngrok execution command was = [ {} ]",
                    ngrokApiClient.getNgrokStatusUrl(), command);
        }
    }

    private String buildNgrokDefaultShellCmd() {
        return ngrokBinaryProvider.getNgrokBinaryFilePath()
                + " http "
                + prepareNgrokConfigParams(ngrokConfigFilePath)
                + this.springServerPort;
    }

    private String buildCustomShellCmd() {
        return ngrokBinaryProvider.getNgrokBinaryFilePath() + " " + ngrokCustomCommand;
    }

    private String prepareNgrokConfigParams(String ngrokConfigFilePath) {
        return StringUtils.isBlank(ngrokConfigFilePath)
                ? "" // no config arguments
                : StringUtils.split(ngrokConfigFilePath, ";").length == 1 ? "-config " + ngrokConfigFilePath + " " // 1 config arg
                : "-config " + String.join(" -config ", StringUtils.split(ngrokConfigFilePath, ";")) + " "; // multiple configs
    }

    private boolean isCustomConfigPresent() {
        return StringUtils.isNotBlank(ngrokCustomCommand);
    }

    private void logTunnelsDetails() {
        List<NgrokTunnel> tunnels = ngrokApiClient.fetchTunnels();

        tunnels.forEach(t -> log.info("Remote url ({})\t-> [ {} ]", t.getProto(), t.getPublicUrl()));
    }
}