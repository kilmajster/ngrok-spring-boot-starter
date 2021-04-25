package ngrok.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrokTunnel implements Serializable {

    private String name;
    private String uri;
    @JsonProperty("public_url")
    private String publicUrl;
    private String proto;
    private NgrokTunnelConfig config;
    private NgrokTunnelMetrics metrics;

    public static class NgrokTunnelConfig {
        private String addr;
        private boolean inspect;

        public NgrokTunnelConfig() {
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public boolean isInspect() {
            return inspect;
        }

        public void setInspect(boolean inspect) {
            this.inspect = inspect;
        }
    }

    public static class NgrokTunnelMetrics {
        private Map<String, Integer> conns;
        private Map<String, Double> http;

        public NgrokTunnelMetrics() {
        }

        public Map<String, Integer> getConns() {
            return conns;
        }

        public void setConns(Map<String, Integer> conns) {
            this.conns = conns;
        }

        public Map<String, Double> getHttp() {
            return http;
        }

        public void setHttp(Map<String, Double> http) {
            this.http = http;
        }
    }

    @JsonIgnore
    public boolean isHttps() {
        return "https".equals(proto);
    }

    @JsonIgnore
    public boolean isHttp() {
        return "http".equals(proto);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
    }

    public NgrokTunnelConfig getConfig() {
        return config;
    }

    public void setConfig(NgrokTunnelConfig config) {
        this.config = config;
    }

    public NgrokTunnelMetrics getMetrics() {
        return metrics;
    @JsonIgnore
    public boolean isHttps() {
        return "https".equals(proto);
    }

    public void setMetrics(NgrokTunnelMetrics metrics) {
        this.metrics = metrics;
    }

}
