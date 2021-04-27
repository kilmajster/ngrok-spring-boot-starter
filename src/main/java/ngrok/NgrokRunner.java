package ngrok;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ngrok.api.NgrokApiClient;
import ngrok.api.model.NgrokTunnel;
import ngrok.configuration.NgrokConfiguration;
import ngrok.configuration.NgrokConfigurationProvider;
import ngrok.exception.NgrokCommandExecuteException;
import ngrok.exception.NgrokDownloadException;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import ngrok.os.NgrokSystemCommandExecutor;
import ngrok.util.NgrokDownloader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;

import java.util.List;

/**
 * For details see <a href="https://github.com/kilmajster/ngrok-spring-boot-starter">docs</a>.
 */
@Slf4j
@RequiredArgsConstructor
public class NgrokRunner {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final NgrokConfiguration ngrokConfiguration;
    private final NgrokApiClient ngrokApiClient;
    private final NgrokBinaryProvider ngrokBinaryProvider;
    private final NgrokConfigurationProvider ngrokConfigurationProvider;
    private final NgrokDownloader ngrokDownloader;
    private final NgrokPlatformDetector ngrokPlatformDetector;
    private final NgrokSystemCommandExecutor ngrokSystemCommandExecutor;
    private final TaskExecutor ngrokExecutor;

    @EventListener
    public void run(WebServerInitializedEvent event) throws NgrokDownloadException, NgrokCommandExecuteException {
        ngrokExecutor.execute(() -> {
            int port = event.getWebServer().getPort();
            if (ngrokIsNotRunning()) {
                if (needToDownloadNgrok()) {
                    downloadAndExtractNgrokBinary();
                    addPermissionsIfNeeded();
                }
                startNgrok(port);
            } else {
                if (ngrokIsListening(port)) {
                    log.info("Ngrok was already running! Dashboard url -> [ {} ]", ngrokApiClient.getNgrokApiUrl());
                } else {
                    startNgrok(port);
                }
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

    private boolean ngrokIsListening(int port) {
        return ngrokApiClient.getTunnel(port).isPresent();
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
        return ngrokBinaryProvider.getNgrokBinaryFilePath() + " " + ngrokConfiguration.getCommand();
    }

    private boolean isCustomConfigPresent() {
        return StringUtils.isNotBlank(ngrokConfiguration.getCommand());
    }

    private void logTunnelsDetails() {
        List<NgrokTunnel> tunnels = ngrokApiClient.fetchTunnels();

        tunnels.forEach(t -> log.info("Remote url ({})\t-> [ {} ]", t.getProto(), t.getPublicUrl()));
    }
}
