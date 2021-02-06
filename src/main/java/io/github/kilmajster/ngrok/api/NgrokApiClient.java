package io.github.kilmajster.ngrok.api;

import io.github.kilmajster.ngrok.api.model.NgrokTunnel;
import io.github.kilmajster.ngrok.api.model.NgrokTunnelsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static io.github.kilmajster.ngrok.NgrokConstants.PROP_NGROK_ENABLED;

@ConditionalOnProperty(name = PROP_NGROK_ENABLED, havingValue = "true")
@Component
public class NgrokApiClient {

    public static final String NGROK_URL_API_TUNNELS = "/api/tunnels";
    public static final String NGROK_URL_HTML_STATUS = "/status";

    private static final Logger log = LoggerFactory.getLogger(NgrokApiClient.class);

    private final RestTemplate restTemplate = new RestTemplate();

    private final String ngrokApiUrl;


    public NgrokApiClient(
            @Value("${ngrok.host:http://127.0.0.1}") String ngrokApiHost,
            @Value("${ngrok.port:4040}") Integer ngrokApiPort) {
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
