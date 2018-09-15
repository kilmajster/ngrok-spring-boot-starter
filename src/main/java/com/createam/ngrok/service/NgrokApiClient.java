package com.createam.ngrok.service;

import com.createam.ngrok.data.Tunnel;
import com.createam.ngrok.data.TunnelsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;


@Component
public class NgrokApiClient {

    public static String NGROK_URL_API_TUNNELS = "/api/tunnels";
    public static String NGROK_URL_HTML_STATUS = "/status";

    private static final Logger log = LoggerFactory.getLogger(NgrokApiClient.class);

    @Value("${ngrok.baseUrl:http://localhost:4040}")
    private String ngrokBaseUrl;

    private final RestTemplate ngrokRestTemplate = new RestTemplate();

    public List<Tunnel> fetchTunnels() {
        try {
            return ngrokRestTemplate.getForObject(resourceRequest(NGROK_URL_API_TUNNELS), TunnelsList.class).getTunnels();
        } catch (Exception e) {
            log.warn("Cannot reach ngrok API on {}! Probably ngrok is not running!", ngrokBaseUrl);
            return Collections.emptyList();
        }
    }

    public String fetchStatusPageAsHtmlString() {
        return ngrokRestTemplate.getForObject(resourceRequest(NGROK_URL_HTML_STATUS), String.class);
    }

    private String resourceRequest(String resourceUrl) {
        return ngrokBaseUrl.concat(resourceUrl);
    }

    public String resourceAsString(String url) {
        try {
            return ngrokRestTemplate.getForObject(resourceRequest(url), String.class);
        } catch (Exception e) {
            log.warn("Cannot reach ngrok API on {}! Probably ngrok is not running!", ngrokBaseUrl);
            return "";
        }
    }
}
