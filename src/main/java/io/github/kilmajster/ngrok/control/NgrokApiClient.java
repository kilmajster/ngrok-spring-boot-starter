package io.github.kilmajster.ngrok.control;

import io.github.kilmajster.ngrok.data.NgrokTunnel;
import io.github.kilmajster.ngrok.data.NgrokTunnelsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@ConditionalOnProperty(name = "ngrok.enabled", havingValue = "true")
@Component
public class NgrokApiClient {

    public static final String NGROK_URL_API_TUNNELS = "/api/tunnels";
    public static final String NGROK_URL_HTML_STATUS = "/status";

    private static final Logger log = LoggerFactory.getLogger(NgrokApiClient.class);

    private final String ngrokApiUrl;

    private RestTemplate restTemplate = new RestTemplate();

    public NgrokApiClient(
            @Value("${ngrok.api.url:http://localhost:4040}") String ngrokApiUrl) {
        this.ngrokApiUrl = ngrokApiUrl;
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
        String ngrokStatusUrl = ngrokApiUrl + NGROK_URL_HTML_STATUS;

        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(ngrokStatusUrl, Void.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            log.warn("Ngrok API not responding at {}, ", ngrokStatusUrl);
        }

        return false;
    }

    public String getNgrokApiUrl() {
        return ngrokApiUrl;
    }
}
