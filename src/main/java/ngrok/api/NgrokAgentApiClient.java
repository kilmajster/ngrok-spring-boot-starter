package ngrok.api;

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
import java.util.stream.Collectors;

/**
 * Representation of Ngrok Agent API.
 * See <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/">docs</a>.
 */
@Slf4j
@NgrokComponent
public class NgrokAgentApiClient {

    private final RestTemplate restTemplate;
    public static final String URI_NGROK_API_TUNNELS = "/api/tunnels";
    public static final String URI_NGROK_API_TUNNEL_DETAIL = "/api/tunnels/{tunnelName}";
    public static final String URI_NGROK_API_CAPTURED_REQUESTS = "/api/requests/http";
    public static final String URI_NGROK_API_CAPTURED_REQUEST_DETAILS = "/api/requests/http/{requestId}";
    public static final String URI_NGROK_HTML_STATUS = "/status";

    @Getter
    private final String ngrokApiUrl;

    public NgrokAgentApiClient(NgrokConfiguration ngrokConfiguration) {
        this.restTemplate = new RestTemplate();
        this.ngrokApiUrl = ngrokConfiguration.getHost() + ":" + ngrokConfiguration.getPort();
    }

    /**
     * See <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/#list-tunnels">docs</a>.
     *
     * @return a list of running tunnels with status and metrics information.
     */
    public List<NgrokTunnel> listTunnels() {
        try {
            return restTemplate.getForObject(
                    apiUrlOf(URI_NGROK_API_TUNNELS),
                    NgrokTunnelsList.class
            ).getTunnels();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    /**
     * See <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/#list-tunnels">docs</a>.
     *
     * @param port port to filter tunnels by
     * @return returns a list of running tunnels with status and metrics information.
     */
    public List<NgrokTunnel> listTunnels(int port) {
        return listTunnels()
                .stream()
                .filter(it -> it.getConfig().getAddr().endsWith(String.valueOf(port)))
                .collect(Collectors.toList());
    }

    private String apiUrlOf(final String apiUri) {
        return ngrokApiUrl + apiUri;
    }

    /**
     * See <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/#start-tunnel">docs</a>.
     *
     * @param addr  forward traffic to this local port number or network address, this can be just a port
     * @param proto the tunnel protocol name. This defines the type of tunnel you would like to start
     * @param name  name of a tunnel to start
     * @return started tunnel details
     */
    public NgrokTunnel startTunnel(final int addr, final String proto, final String name) {
        try {
            final ResponseEntity<NgrokTunnel> startTunnelResponse = restTemplate.postForEntity(
                    apiUrlOf(URI_NGROK_API_TUNNELS),
                    NgrokStartTunnel.of(addr, proto, name),
                    NgrokTunnel.class
            );
            if (startTunnelResponse.getStatusCode().value() != 201) {
                log.warn("Failed to start ngrok tunnel!");
            }
            return startTunnelResponse.getBody();
        } catch (Exception ex) {
            throw new NgrokApiException("Failed to start ngrok tunnel!", ex);
        }
    }

    /**
     * See <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/#tunnel-detail">docs</a>.
     *
     * @param tunnelName name of a tunnel to fetch details of
     * @return status and metrics about the named running tunnel.
     */
    public NgrokTunnel tunnelDetail(final String tunnelName) {
        try {
            return restTemplate.getForObject(
                    apiUrlOf(URI_NGROK_API_TUNNEL_DETAIL),
                    NgrokTunnel.class,
                    tunnelName
            );
        } catch (Exception ex) {
            throw new NgrokApiException("Failed to fetch details of ngrok tunnel with tunnelName = " + tunnelName, ex);
        }
    }

    /**
     * See <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/#stop-tunnel">docs</a>.
     *
     * @param tunnelName name of a tunnel to stop
     * @return true when tunnel was stopped successfully
     */
    public boolean stopTunnel(final String tunnelName) {
        try {
            return restTemplate.exchange(
                    apiUrlOf(URI_NGROK_API_TUNNEL_DETAIL),
                    HttpMethod.DELETE,
                    null,
                    Void.class,
                    tunnelName
            ).getStatusCode().value() == 204;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * See <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/#list-captured-requests">docs</a>.
     *
     * @param limit      maximum number of requests to return
     * @param tunnelName filter requests only for the given tunnel name
     * @return list of captured requests. See the
     * <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/#captured-request-detail">
     * Captured Request Detail</a> resource for docs on the request objects.
     */
    public List<NgrokCapturedRequest> listCapturedRequests(final Integer limit, final String tunnelName) {
        try {
            return restTemplate.getForObject(UriComponentsBuilder.fromUriString(
                                    apiUrlOf(URI_NGROK_API_CAPTURED_REQUESTS))
                            .queryParamIfPresent("limit", Optional.ofNullable(limit))
                            .queryParamIfPresent("tunnel_name", Optional.ofNullable(tunnelName))
                            .toUriString(),
                    NgrokCapturedRequestsList.class
            ).getRequests();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<NgrokCapturedRequest> listCapturedRequests(final Integer limit) {
        return listCapturedRequests(limit, null);
    }

    public List<NgrokCapturedRequest> listCapturedRequests(final String tunnelName) {
        return listCapturedRequests(null, tunnelName);
    }

    public List<NgrokCapturedRequest> listCapturedRequests() {
        return listCapturedRequests(null, null);
    }

    /**
     * See <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/#replay-captured-request">docs</a>.
     *
     * @param id         id of request to replay
     * @param tunnelName name of the tunnel to play the request against. If unspecified, the request is played against
     *                   the same tunnel it was recorded on
     * @return true when successfully replayed captured request
     */
    public boolean replayCapturedRequest(String id, String tunnelName) {
        try {
            return restTemplate.postForEntity(
                    apiUrlOf(URI_NGROK_API_CAPTURED_REQUESTS),
                    NgrokReplayCapturedRequest.of(id, tunnelName),
                    Void.class
            ).getStatusCode().value() == 204;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * See <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/#delete-captured-requests">docs</a>.
     *
     * @return true when requests were deleted
     */
    public boolean deleteCapturedRequests() {
        try {
            return restTemplate.exchange(
                    apiUrlOf(URI_NGROK_API_CAPTURED_REQUESTS),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            ).getStatusCode().value() == 204;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * See <a href="https://ngrok.com/docs/secure-tunnels/ngrok-agent/reference/api/#captured-request-detail">docs</a>.
     *
     * @param requestId id of a request
     * @return metadata and raw bytes of a captured request. The raw data is base64-encoded in the JSON
     * response. The <code>response</code> value maybe <code>null</code> if the local server has not yet responded to a
     * request.
     */
    public NgrokCapturedRequest capturedRequestDetail(String requestId) {
        try {
            return restTemplate.getForObject(
                    apiUrlOf(URI_NGROK_API_CAPTURED_REQUEST_DETAILS),
                    NgrokCapturedRequest.class,
                    requestId
            );
        } catch (Exception ex) {
            throw new NgrokApiException("Failed to fetch details of ngrok request with requestId = " + requestId, ex);
        }
    }

    /**
     * @return true if ngrok is running.
     */
    public boolean isResponding() {
        try {
            return restTemplate.getForEntity(
                    getNgrokStatusUrl(),
                    Void.class
            ).getStatusCode().value() == 200;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getNgrokStatusUrl() {
        return ngrokApiUrl + URI_NGROK_HTML_STATUS;
    }

    /**
     * @return http tunnel URL or null in case ngrok is not running.
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
     * @return https tunnel URL or null in case ngrok is not running.
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
     * Please switch to <code>isResponding()</code> method.
     *
     * @return true if ngrok is running.
     */
    @Deprecated
    public boolean isRunning() {
        return isResponding();
    }
}
