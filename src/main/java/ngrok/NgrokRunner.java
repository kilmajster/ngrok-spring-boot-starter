package ngrok;

import ngrok.api.NgrokApiClient;
import ngrok.api.model.NgrokTunnel;
import ngrok.configuration.NgrokConfigurationProvider;
import ngrok.exception.NgrokCommandExecuteException;
import ngrok.exception.NgrokDownloadException;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import ngrok.os.NgrokSystemCommandExecutor;
import ngrok.util.NgrokDownloader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;

import java.util.List;

/**
 * For details see <a href="https://github.com/kilmajster/ngrok-spring-boot-starter">docs</a>.
 */
public class NgrokRunner {

    private static final Logger log = LoggerFactory.getLogger(NgrokRunner.class);

    @Value("${" + NgrokProperties.NGROK_COMMAND + ":}")
    private String ngrokCustomCommand;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private final NgrokApiClient ngrokApiClient;
    private final NgrokBinaryProvider ngrokBinaryProvider;
    private final NgrokConfigurationProvider ngrokConfigurationProvider;
    private final NgrokDownloader ngrokDownloader;
    private final NgrokPlatformDetector ngrokPlatformDetector;
    private final NgrokSystemCommandExecutor ngrokSystemCommandExecutor;
    private final TaskExecutor ngrokExecutor;

    public NgrokRunner(
            NgrokApiClient ngrokApiClient, NgrokBinaryProvider ngrokBinaryProvider,
            NgrokConfigurationProvider ngrokConfigurationProvider, NgrokDownloader ngrokDownloader,
            NgrokPlatformDetector ngrokPlatformDetector, NgrokSystemCommandExecutor ngrokSystemCommandExecutor,
            TaskExecutor ngrokExecutor) {
        this.ngrokApiClient = ngrokApiClient;
        this.ngrokBinaryProvider = ngrokBinaryProvider;
        this.ngrokConfigurationProvider = ngrokConfigurationProvider;
        this.ngrokDownloader = ngrokDownloader;
        this.ngrokPlatformDetector = ngrokPlatformDetector;
        this.ngrokSystemCommandExecutor = ngrokSystemCommandExecutor;
        this.ngrokExecutor = ngrokExecutor;
    }

    @EventListener
    public void run(WebServerInitializedEvent event) throws NgrokDownloadException, NgrokCommandExecuteException {
        ngrokExecutor.execute(() -> {
            if (ngrokIsNotRunning()) {
                if (needToDownloadNgrok()) {
                    downloadAndExtractNgrokBinary();
                    addPermissionsIfNeeded();
                }
                startNgrok(event.getWebServer().getPort());
            } else {
                log.info("Ngrok was already running! Dashboard url -> [ {} ]", ngrokApiClient.getNgrokApiUrl());
            }
            logTunnelsDetails();
            applicationEventPublisher.publishEvent(new NgrokInitializedEvent(this, ngrokApiClient.fetchTunnels()));
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

    private void startNgrok(int springServerPort) {
        String command = isCustomConfigPresent() ? buildCustomShellCmd() : buildNgrokDefaultShellCmd(springServerPort);
        log.debug("Starting ngrok with command = [ {} ]", command);

        ngrokSystemCommandExecutor.execute(command);

        if (ngrokApiClient.isResponding()) {
            log.info("Ngrok started successfully! Dashboard url -> [ {} ]", ngrokApiClient.getNgrokApiUrl());
        } else {
            log.warn("Ngrok seems to not responding! Ngrok status url = [ {} ] Ngrok execution command was = [ {} ]",
                    ngrokApiClient.getNgrokStatusUrl(), command);
        }
    }

    private String buildNgrokDefaultShellCmd(int springServerPort) {
        return ngrokBinaryProvider.getNgrokBinaryFilePath()
                + " http "
                + ngrokConfigurationProvider.prepareNgrokConfigParams()
                + springServerPort;
    }

    private String buildCustomShellCmd() {
        return ngrokBinaryProvider.getNgrokBinaryFilePath() + " " + ngrokCustomCommand;
    }

    private boolean isCustomConfigPresent() {
        return StringUtils.isNotBlank(ngrokCustomCommand);
    }

    private void logTunnelsDetails() {
        List<NgrokTunnel> tunnels = ngrokApiClient.fetchTunnels();

        tunnels.forEach(t -> log.info("Remote url ({})\t-> [ {} ]", t.getProto(), t.getPublicUrl()));
    }
}