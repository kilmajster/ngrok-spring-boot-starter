package ngrok.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrokTunnel implements Serializable {

    private String name;
    private String uri;
    @JsonProperty("public_url")
    private String publicUrl;
    private String proto;
    private NgrokTunnelConfig config;
    private NgrokTunnelMetrics metrics;

    @Tolerate
    public NgrokTunnel() {
    }

    @JsonIgnore
    public boolean isHttps() {
        return "https".equals(proto);
    }

    @JsonIgnore
    public boolean isHttp() {
        return "http".equals(proto);
    }

    @Getter
    @Setter
    @Builder
    public static class NgrokTunnelConfig {

        private String addr;
        private boolean inspect;

        @Tolerate
        public NgrokTunnelConfig() {
        }
    }

    @Getter
    @Setter
    @Builder
    public static class NgrokTunnelMetrics {

        private Map<String, Double> conns;
        private Map<String, Double> http;

        @Tolerate
        public NgrokTunnelMetrics() {
        }
    }
}