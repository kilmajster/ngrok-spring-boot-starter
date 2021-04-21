package ngrok.api;

import lombok.extern.slf4j.Slf4j;
import ngrok.NgrokComponent;
import ngrok.api.model.NgrokTunnel;
import ngrok.api.model.NgrokTunnelsList;
import ngrok.configuration.NgrokConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@NgrokComponent
public class NgrokApiClient {

    public static final String NGROK_URL_API_TUNNELS = "/api/tunnels";
    public static final String NGROK_URL_HTML_STATUS = "/status";

    private final RestTemplate restTemplate = new RestTemplate();

    private final String ngrokApiUrl;

    public NgrokApiClient(NgrokConfiguration ngrokConfiguration) {
        this.ngrokApiUrl = ngrokConfiguration.getHost() + ":" + ngrokConfiguration.getPort();
    }

    public List<NgrokTunnel> fetchTunnels() {
        try {
            NgrokTunnelsList tunnels = restTemplate.getForObject(ngrokApiUrl + NGROK_URL_API_TUNNELS, NgrokTunnelsList.class);

            assert tunnels != null;
            return tunnels.getTunnels();
        } catch (Exception e) {
            return Collections.emptyList();
        }
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

    public String getNgrokApiUrl() {
        return ngrokApiUrl;
    }

    public String getNgrokStatusUrl() {
        return ngrokApiUrl + NGROK_URL_HTML_STATUS;
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
