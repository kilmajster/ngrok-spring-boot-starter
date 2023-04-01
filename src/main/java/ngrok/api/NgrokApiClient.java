package ngrok.api;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ngrok.NgrokComponent;
import ngrok.api.model.NgrokTunnel;
import ngrok.api.model.NgrokTunnelsList;
import ngrok.api.rquest.NgrokStartTunnel;
import ngrok.configuration.NgrokConfiguration;
import ngrok.exception.NgrokApiException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate  ;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NgrokComponent
public class NgrokApiClient {

    public static final String URI_NGROK_API_TUNNELS = "/api/tunnels";
    public static final String URI_NGROK_API_TUNNEL_DETAIL = "/api/tunnels/{tunnelName}";
    public static final String URI_NGROK_HTML_STATUS = "/status";

    @Getter
    private final String ngrokApiUrl;
    private final RestTemplate restTemplate;

    public NgrokApiClient(NgrokConfiguration ngrokConfiguration) {
        this.restTemplate = new RestTemplate();
        this.ngrokApiUrl = ngrokConfiguration.getHost() + ":" + ngrokConfiguration.getPort();
    }

    /**
     * Returns a list of running tunnels with status and metrics information.
     */
    public List<NgrokTunnel> listTunnels() {
        return Try.of(() -> restTemplate
                .getForObject(
                        apiUrlOf(URI_NGROK_API_TUNNELS),
                        NgrokTunnelsList.class
                ).getTunnels()
        ).getOrElse(Collections.emptyList());
    }

    public List<NgrokTunnel> listTunnels(final int port) {
        return listTunnels()
                .stream()
                .filter(it -> it.getConfig().getAddr().endsWith(String.valueOf(port)))
                .collect(Collectors.toList());
    }

    private String apiUrlOf(final String apiUri) {
        return ngrokApiUrl + apiUri;
    }

    /**
     * Dynamically starts a new tunnel on the ngrok client. The request body parameters are the same
     * as those you would use to define the tunnel in the configuration file.
     */
    public NgrokTunnel startTunnel(final int addr, final String proto, final String name) {
        return Try.of(() -> {
                    final ResponseEntity<NgrokTunnel> startTunnelResponse = restTemplate.postForEntity(
                            apiUrlOf(URI_NGROK_API_TUNNELS),
                            NgrokStartTunnel.of(addr, proto, name),
                            NgrokTunnel.class);
                    if (startTunnelResponse.getStatusCode().isError()) {
                        log.warn("Failed to start ngrok tunnel!");
                    }
                    return startTunnelResponse.getBody();
                }
        ).getOrElseThrow(t -> new NgrokApiException("Failed to start ngrok tunnel!", t));
    }

    /**
     * Get status and metrics about the named running tunnel.
     */
    public NgrokTunnel tunnelDetail(final String tunnelName) {
        return Try.of(() -> restTemplate
                .getForObject(
                        apiUrlOf(URI_NGROK_API_TUNNEL_DETAIL),
                        NgrokTunnel.class,
                        tunnelName
                )
        ).getOrElseThrow(t -> new NgrokApiException("Failed to fetch details of ngrok tunnel with tunnelName = " + tunnelName, t));
    }

    /**
     * Stop a running tunnel.
     * Returns true when tunnel was stopped successfully.
     */
    public boolean stopTunnel(final String tunnelName) {
        return Try.of(() -> restTemplate
                .exchange(
                        apiUrlOf(URI_NGROK_API_TUNNEL_DETAIL),
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        tunnelName
                ).getStatusCode().is2xxSuccessful()
        ).getOrElse(Boolean.FALSE);
    }

    public boolean isResponding() {
        return Try.of(() -> restTemplate
                .getForEntity(
                        getNgrokStatusUrl(),
                        Void.class
                ).getStatusCode().is2xxSuccessful()
        ).getOrElse(Boolean.FALSE);
    }

    public String getNgrokStatusUrl() {
        return ngrokApiUrl + URI_NGROK_HTML_STATUS;
    }
}