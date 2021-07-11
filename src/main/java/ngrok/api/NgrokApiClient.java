package ngrok.api;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ngrok.NgrokComponent;
import ngrok.api.model.NgrokCapturedRequest;
import ngrok.api.model.NgrokCapturedRequestsList;
import ngrok.api.model.NgrokTunnel;
import ngrok.api.model.NgrokTunnelsList;
import ngrok.api.rquest.NgrokReplayCapturedRequest;
import ngrok.api.rquest.NgrokStartTunnel;
import ngrok.configuration.NgrokConfiguration;
import ngrok.exception.NgrokApiException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@NgrokComponent
public class NgrokApiClient {

    private final RestTemplate restTemplate;
    public static final String URI_NGROK_API_TUNNELS = "/api/tunnels";
    public static final String URI_NGROK_API_TUNNEL_DETAIL = "/api/tunnels/{tunnelName}";
    public static final String URI_NGROK_API_CAPTURED_REQUESTS = "/api/requests/http";
    public static final String URI_NGROK_API_CAPTURED_REQUEST_DETAILS = "/api/requests/http/{requestId}";
    public static final String URI_NGROK_HTML_STATUS = "/status";

    @Getter
    private final String ngrokApiUrl;

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

    /**
     * Returns a list of all HTTP requests captured for inspection. This will only return requests
     * that are still in memory (ngrok evicts captured requests when their memory usage exceeds inspect_db_size)
     */
    public List<NgrokCapturedRequest> listCapturedRequests(final int limit, final String tunnelName) {
        return Try.of(() -> restTemplate
                .getForObject(UriComponentsBuilder.fromUriString(
                        apiUrlOf(URI_NGROK_API_CAPTURED_REQUESTS))
                                .queryParamIfPresent("limit", limit > 0 ? Optional.of(limit) : Optional.empty())
                                .queryParamIfPresent("tunnel_name", Optional.ofNullable(tunnelName))
                                .toUriString(),
                        NgrokCapturedRequestsList.class
                ).getRequests()
        ).getOrElse(Collections.emptyList());
    }

    public List<NgrokCapturedRequest> listCapturedRequests(final int limit) {
        return listCapturedRequests(limit, null);
    }

    public List<NgrokCapturedRequest> listCapturedRequests(final String tunnelName) {
        return listCapturedRequests(-1, tunnelName);
    }

    public List<NgrokCapturedRequest> listCapturedRequests() {
        return listCapturedRequests(-1, null);
    }

    /**
     * Replays a request against the local endpoint of a tunnel.
     */
    public boolean replayCapturedRequest(String id, String tunnelName) {
        return Try.of(() -> restTemplate
                .postForEntity(
                        apiUrlOf(URI_NGROK_API_CAPTURED_REQUESTS),
                        NgrokReplayCapturedRequest.of(id, tunnelName),
                        Void.class
                ).getStatusCode().is2xxSuccessful()
        ).getOrElse(Boolean.FALSE);
    }

    /**
     * Deletes all captured requests.
     */
    public boolean deleteCapturedRequests() {
        return Try.of(() -> restTemplate
                .exchange(
                        apiUrlOf(URI_NGROK_API_CAPTURED_REQUESTS),
                        HttpMethod.DELETE,
                        null,
                        Void.class
                ).getStatusCode().is2xxSuccessful()
        ).getOrElse(Boolean.FALSE);
    }

    /**
     * Returns metadata and raw bytes of a captured request. The raw data is base64-encoded in the JSON response.
     * The response value maybe null if the local server has not yet responded to a request.
     */
    public NgrokCapturedRequest capturedRequestDetail(String requestId) {
        return Try.of(() -> restTemplate
                .getForObject(
                        apiUrlOf(URI_NGROK_API_CAPTURED_REQUEST_DETAILS),
                        NgrokCapturedRequest.class,
                        requestId
                )
        ).getOrElseThrow(t -> new NgrokApiException("Failed to fetch details of ngrok request with requestId = " + requestId, t));
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

    /**
     * Returns http tunnel URL or null in case ngrok is not running.
     *
     * @return http tunnel url
     */
    public String getHttpTunnelUrl() {
        return listTunnels()
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
        return listTunnels()
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