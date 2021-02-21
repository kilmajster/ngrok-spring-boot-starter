package ngrok.api;

import ngrok.NgrokComponent;
import ngrok.NgrokProperties;
import ngrok.api.model.NgrokTunnel;
import ngrok.api.model.NgrokTunnelsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@NgrokComponent
public class NgrokApiClient {

    public static final String NGROK_URL_API_TUNNELS = "/api/tunnels";
    public static final String NGROK_URL_HTML_STATUS = "/status";

    private static final Logger log = LoggerFactory.getLogger(NgrokApiClient.class);

    private final RestTemplate restTemplate = new RestTemplate();

    private final String ngrokApiUrl;

    public NgrokApiClient(
            @Value("${" + NgrokProperties.NGROK_HOST + ":" + NgrokProperties.NGROK_HOST_DEFAULT + "}") String ngrokApiHost,
            @Value("${" + NgrokProperties.NGROK_PORT + ":" + NgrokProperties.NGROK_PORT_DEFAULT + "}") Integer ngrokApiPort) {
        this.ngrokApiUrl = ngrokApiHost + ":" + ngrokApiPort;
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
}