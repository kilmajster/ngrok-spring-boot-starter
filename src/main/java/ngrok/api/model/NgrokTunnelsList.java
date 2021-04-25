package ngrok.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrokTunnelsList implements Serializable {

    private String uri;
    private List<NgrokTunnel> tunnels;

}

    public void setTunnels(List<NgrokTunnel> tunnels) {
        this.tunnels = tunnels;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}