package ngrok.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrokCapturedRequest implements Serializable {

    private String uri;
    private String id;
    @JsonProperty("tunnel_name")
    private String tunnelName;
    @JsonProperty("remote_addr")
    private String remoteAddr;
    private Date start;
    private int duration;
    private NgrokRequest request;
    private NgrokResponse response;

    public static class NgrokRequest {
        @JsonSerialize()
        private String method;
        private String proto;
        @JsonUnwrapped
        private NgrokHeaders headers;
        private String uri;
        private String raw;

        public NgrokRequest() {
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getProto() {
            return proto;
        }

        public void setProto(String proto) {
            this.proto = proto;
        }

        public NgrokHeaders getHeaders() {
            return headers;
        }

        public void setHeaders(NgrokHeaders headers) {
            this.headers = headers;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getRaw() {
            return raw;
        }

        public void setRaw(String raw) {
            this.raw = raw;
        }
    }

    public static class NgrokResponse {
        private String status;
        @JsonProperty("status_code")
        private int statusCode;
        private String proto;
        @JsonUnwrapped
        private NgrokHeaders headers;
        private String raw;

        public NgrokResponse() {
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getProto() {
            return proto;
        }

        public void setProto(String proto) {
            this.proto = proto;
        }

        public NgrokHeaders getHeaders() {
            return headers;
        }

        public void setHeaders(NgrokHeaders headers) {
            this.headers = headers;
        }

        public String getRaw() {
            return raw;
        }

        public void setRaw(String raw) {
            this.raw = raw;
        }
    }

    public static class NgrokHeaders {
        private Map<String, List<String>> headers;

        public NgrokHeaders() {
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, List<String>> headers) {
            this.headers = headers;
        }
    }

    public NgrokCapturedRequest() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public NgrokRequest getRequest() {
        return request;
    }

    public void setRequest(NgrokRequest request) {
        this.request = request;
    }

    public NgrokResponse getResponse() {
        return response;
    }

    public void setResponse(NgrokResponse response) {
        this.response = response;
    }
}
