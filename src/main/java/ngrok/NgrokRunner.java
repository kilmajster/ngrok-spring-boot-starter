package ngrok;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ngrok.api.NgrokAgentApiClient;
import ngrok.api.model.NgrokTunnel;
import ngrok.configuration.NgrokConfiguration;
import ngrok.configuration.NgrokConfigurationProvider;
import ngrok.exception.NgrokCommandExecuteException;
import ngrok.exception.NgrokDownloadException;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import ngrok.os.NgrokSystemCommandExecutor;
import ngrok.download.NgrokDownloader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.server.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * For details see <a href="https://github.com/kilmajster/ngrok-spring-boot-starter">docs</a>.
 */
@Slf4j
@RequiredArgsConstructor
public class NgrokRunner {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final NgrokConfiguration ngrokConfiguration;
    private final NgrokAgentApiClient ngrokAgentApiClient;
    private final NgrokBinaryProvider ngrokBinaryProvider;
    private final NgrokConfigurationProvider ngrokConfigurationProvider;
    private final NgrokDownloader ngrokDownloader;
    private final NgrokPlatformDetector ngrokPlatformDetector;
    private final NgrokSystemCommandExecutor ngrokSystemCommandExecutor;
    private final TaskExecutor ngrokExecutor;
    private final String applicationName;

    @EventListener
    public void run(WebServerInitializedEvent event) throws NgrokDownloadException, NgrokCommandExecuteException {
        ngrokExecutor.execute(() -> {
            int port = event.getWebServer().getPort();
            List<NgrokTunnel> tunnels;
            if (ngrokIsNotRunning()) {
                if (needToDownloadNgrok()) {
                    downloadAndExtractNgrokBinary();
                    addPermissionsIfNeeded();
                }

                configureAuthTokenOrLogWarn();

                startNgrok(port);
                tunnels = ngrokAgentApiClient.listTunnels(port);
            } else {
                if (ngrokIsListening(port)) {
                    log.info("Ngrok was already running! Dashboard url -> [ {} ]", ngrokAgentApiClient.getNgrokApiUrl());
                    tunnels = ngrokAgentApiClient.listTunnels(port);
                } else {
                    NgrokTunnel httpsTunnel = ngrokAgentApiClient.startTunnel(port, "http", applicationName + "-http-" + port);
                    if (Objects.nonNull(httpsTunnel)) {
                        log.info("New Ngrok tunnel added -> [ {}: {} ]", httpsTunnel.getName(), httpsTunnel.getPublicUrl());
                    }
                    NgrokTunnel httpTunnel = ngrokAgentApiClient.tunnelDetail(applicationName + "-http-" + port + " (http)");
                    if (Objects.nonNull(httpTunnel)) {
                        log.info("New Ngrok tunnel added -> [ {}: {} ]", httpTunnel.getName(), httpTunnel.getPublicUrl());
                    }
                    tunnels = listOf(httpTunnel, httpsTunnel);
                }
            }
            logTunnelsDetails(tunnels);
            applicationEventPublisher.publishEvent(new NgrokInitializedEvent(this, tunnels));
        });
    }

    private void configureAuthTokenOrLogWarn() {
        final String ngrokDirectory = ngrokBinaryProvider.getNgrokDirectoryOrDefault();
        if (!ngrokConfigurationProvider.isAuthTokenConfigured(ngrokDirectory)) {
            if (!ngrokConfigurationProvider.isAuthTokenPresent(ngrokDirectory)) {
                log.warn("Ngrok auth token is missing! For your personal auth token visit https://dashboard.ngrok.com/get-started/your-authtoken " +
                        "and then add it as ngrok.authToken=<YOUR AUTH TOKEN> to application.properties or to your ngrok configuration file.");
            } else {
                configureAuthToken();
            }
        }
    }

    private void configureAuthToken() {
        String command = ngrokBinaryProvider.getNgrokBinaryFilePath()
                + " authtoken "
                + ngrokConfiguration.getAuthToken();

        ngrokSystemCommandExecutor.execute(command);
    }

    @SafeVarargs
    private static <T> List<T> listOf(T... args) {
        return Stream.of(args).filter(Objects::nonNull).collect(Collectors.toList());
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
        return !ngrokAgentApiClient.isResponding();
    }

    private boolean ngrokIsListening(int port) {
        return !ngrokAgentApiClient.listTunnels(port).isEmpty();
    }

    private boolean needToDownloadNgrok() {
        return !ngrokBinaryProvider.isNgrokBinaryPresent();
    }

    private void startNgrok(int springServerPort) {
        String command = isCustomConfigPresent() ? buildCustomShellCmd() : buildNgrokDefaultShellCmd(springServerPort);
        log.debug("Starting ngrok with command = [ {} ]", command);

        ngrokSystemCommandExecutor.execute(command);

        if (ngrokAgentApiClient.isResponding()) {
            log.info("Ngrok started successfully! Dashboard url -> [ {} ]", ngrokAgentApiClient.getNgrokApiUrl());
        } else {
            log.warn("Ngrok seems to not responding! Ngrok status url = [ {} ] Ngrok execution command was = [ {} ]",
                    ngrokAgentApiClient.getNgrokStatusUrl(), command);
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

    private static void logTunnelsDetails(List<NgrokTunnel> tunnels) {
        tunnels.forEach(t -> log.info("Remote url ({})\t-> [ {} ]", t.getProto(), t.getPublicUrl()));
    }
}
