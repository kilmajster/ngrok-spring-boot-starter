package ngrok.api;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ngrok.NgrokComponent;
import ngrok.api.model.NgrokTunnel;
import ngrok.api.model.NgrokTunnelsList;
import ngrok.configuration.NgrokConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@NgrokComponent
public class NgrokApiClient {

    public static final String NGROK_URL_API_TUNNELS = "/api/tunnels";
    public static final String NGROK_URL_HTML_STATUS = "/status";
    public static final String URI_NGROK_API_TUNNEL_DETAIL = "/api/tunnels/{tunnelName}";

    private final RestTemplate restTemplate;

    @Getter
    private final String ngrokApiUrl;

    public NgrokApiClient(NgrokConfiguration ngrokConfiguration) {
        this.restTemplate = new RestTemplate();
        this.ngrokApiUrl = ngrokConfiguration.getHost() + ":" + ngrokConfiguration.getPort();
    }

    public List<NgrokTunnel> fetchTunnels() {
        try {
            NgrokTunnelsList tunnels = restTemplate.getForObject(getNgrokTunnelsUrl(), NgrokTunnelsList.class);

            assert tunnels != null;
            return tunnels.getTunnels();
        } catch (Exception e) {
            log.error("Ngrok API error when fetch", e);
            return Collections.emptyList();
        }
    }

    public List<NgrokTunnel> fetchTunnels(int port) {
        return fetchTunnels()
                .stream()
                .filter(it -> it.getConfig().getAddr().getPort() == port)
                .collect(Collectors.toList());
    }

    public NgrokTunnel startTunnel(int port, String proto, String name) {
        Map<String, String> request = new HashMap<>();
        request.put("addr", String.valueOf(port));
        request.put("proto", proto);
        request.put("name", name);
        return restTemplate.postForObject(getNgrokTunnelsUrl(), request, NgrokTunnel.class);
    }

    public NgrokTunnel tunnelDetail(final String tunnelName) {
        return restTemplate.getForObject(apiUrlOf(URI_NGROK_API_TUNNEL_DETAIL), NgrokTunnel.class, tunnelName);
    }

    public boolean isResponding() {
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(getNgrokStatusUrl(), Void.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            log.debug("Ngrok API not responding at {}", getNgrokStatusUrl());
        }

        return false;
    }

    public String getNgrokStatusUrl() {
        return ngrokApiUrl + NGROK_URL_HTML_STATUS;
    }

    public String getNgrokTunnelsUrl() {
        return ngrokApiUrl + NGROK_URL_API_TUNNELS;
    }

    private String apiUrlOf(final String apiUri) {
        return ngrokApiUrl + apiUri;
    }

    /**
     * Returns http tunnel URL or null in case ngrok is not running.
     *
     * @return http tunnel url
     */
    public String getHttpTunnelUrl() {
        return fetchTunnels()
                .stream()
                .filter(NgrokTunnel::isHttp)
                .findFirst()
                .map(NgrokTunnel::getPublicUrl)
                .orElse(null);
    }

    /**
     * Returns https tunnel URL or null in case ngrok is not running.
     *
     * @return https tunnel url
     */
    public String getHttpsTunnelUrl() {
        return fetchTunnels()
                .stream()
                .filter(NgrokTunnel::isHttps)
                .findFirst()
                .map(NgrokTunnel::getPublicUrl)
                .orElse(null);
    }

    /**
     * @return true if ngrok is running
     */
    public boolean isRunning() {
        return isResponding();
    }
}
