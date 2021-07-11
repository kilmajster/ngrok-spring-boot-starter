package ngrok.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
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

    @Tolerate
    public NgrokCapturedRequest() {
    }

    @Getter
    @Setter
    public static class NgrokRequest {

        @JsonSerialize()
        private String method;
        private String proto;
        @JsonUnwrapped
        private NgrokHeaders headers;
        private String uri;
        private String raw;

        @Tolerate
        public NgrokRequest() {
        }
    }

    @Getter
    @Setter
    public static class NgrokResponse {

        private String status;
        @JsonProperty("status_code")
        private int statusCode;
        private String proto;
        @JsonUnwrapped
        private NgrokHeaders headers;
        private String raw;

        @Tolerate
        public NgrokResponse() {
        }
    }

    @Getter
    @Setter
    public static class NgrokHeaders {

        private Map<String, List<String>> headers;

        @Tolerate
        public NgrokHeaders() {
        }

    }
}