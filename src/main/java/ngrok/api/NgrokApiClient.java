package ngrok.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ngrok.NgrokComponent;
import ngrok.NgrokProperties;
import ngrok.api.model.NgrokCapturedRequest;
import ngrok.api.model.NgrokCapturedRequestsList;
import ngrok.api.model.NgrokTunnel;
import ngrok.api.model.NgrokTunnelsList;
import ngrok.configuration.NgrokConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import ngrok.exception.NgrokApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@NgrokComponent
public class NgrokApiClient {

    public static final String NGROK_URL_API_TUNNELS = "/api/tunnels";
    public static final String NGROK_URL_HTML_STATUS = "/status";

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
    public NgrokTunnel startTunnel(final String addr, final String proto, final String name) {
        return Try.of(() -> restTemplate
                .postForObject(
                        apiUrlOf(URI_NGROK_API_TUNNELS),
                        NgrokStartTunnel.of(addr, proto, name),
                        NgrokTunnel.class
                )
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
                        HttpMethod.DELETE, null,
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
                .getForObject(
                        apiUrlOf(URI_NGROK_API_CAPTURED_REQUESTS)
                                + asQueryParam("limit", limit)
                                + asQueryParam("tunnel_name", tunnelName),
                        NgrokCapturedRequestsList.class
                ).getRequests()
        ).getOrElse(Collections.emptyList());
    }

    private String asQueryParam(final String name, final String value) {
        return Objects.nonNull(name) && Objects.nonNull(value)
                ? "&" + name + "=" + value
                : "";
    }

    private String asQueryParam(final String name, final int value) {
        return Objects.nonNull(name) && value > 0
                ? "&" + name + "=" + value
                : "";
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
                        ReplayCapturedRequest.of(id, tunnelName),
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

    public static class NgrokStartTunnel {

        private String addr;
        private String proto;
        private String name;

        public static NgrokStartTunnel of(String addr, String proto, String name) {
            NgrokStartTunnel ngrokStartTunnel = new NgrokStartTunnel();
            ngrokStartTunnel.setAddr(addr);
            ngrokStartTunnel.setProto(proto);
            ngrokStartTunnel.setName(name);
            return ngrokStartTunnel;
        }


        public NgrokStartTunnel() {
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getProto() {
            return proto;
        }

        public void setProto(String proto) {
            this.proto = proto;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ReplayCapturedRequest {
        private String id;
        @JsonProperty("tunnel_name")
        private String tunnelName;

        public static ReplayCapturedRequest of(String id, String tunnelName) {
            ReplayCapturedRequest replayCapturedRequest = new ReplayCapturedRequest();
            replayCapturedRequest.setId(id);
            replayCapturedRequest.setTunnelName(tunnelName);
            return replayCapturedRequest;
        }

        public ReplayCapturedRequest() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTunnelName() {
            return tunnelName;
        }

        public void setTunnelName(String tunnelName) {
            this.tunnelName = tunnelName;
        }
    }

    public static class NgrokApiErrorResponse {
        @JsonProperty("error_code")
        private int errorCode;
        @JsonProperty("status_code")
        private int statusCode;
        @JsonProperty("msg")
        private String message;
        @JsonUnwrapped
        private NgrokApiErrorResponseDetails details;

        public static class NgrokApiErrorResponseDetails {
            private String err;
        }
        /**
         * {
         *     "error_code": 103,
         *     "status_code": 502,
         *     "msg": "failed to start tunnel",
         *     "details": {
         *         "err": "TCP tunnels are only available after you sign up.\nSign up at: https://dashboard.ngrok.com/signup\n\nIf you have already signed up, make sure your authtoken is installed.\nYour authtoken is available on your dashboard: https://dashboard.ngrok.com/get-started/your-authtoken\r\n\r\nERR_NGROK_302\r\n"
         *     }
         * }
         */
    }
}
