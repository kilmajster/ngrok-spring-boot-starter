package io.github.createam.ngrok.control;

import io.github.createam.ngrok.data.Tunnel;
import io.github.createam.ngrok.data.TunnelsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    public List<Tunnel> fetchTunnels() {
        try {
            TunnelsList tunnels = restTemplate.getForObject(ngrokApiUrl + NGROK_URL_API_TUNNELS, TunnelsList.class);

            assert tunnels != null;
            return tunnels.getTunnels();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public boolean isResponding() {
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(ngrokApiUrl + NGROK_URL_HTML_STATUS, Void.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            log.info("Ngrok API not responding.");
        }

        return false;
    }
}
