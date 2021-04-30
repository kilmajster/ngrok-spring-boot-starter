package ngrok.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrokTunnel implements Serializable {

    private String name;

    @JsonProperty("public_url")
    private String publicUrl;

    private String proto;

    private NgrokTunnelConfig config;

    @JsonIgnore
    public boolean isHttps() {
        return "https".equals(proto);
    }

    @JsonIgnore
    public boolean isHttp() {
        return "http".equals(proto);
    }

}
