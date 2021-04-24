package ngrok.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrokTunnelsList implements Serializable {

    private String uri;
    private List<NgrokTunnel> tunnels;

    public NgrokTunnelsList() {
        this.tunnels = new ArrayList<>();
    }

    public List<NgrokTunnel> getTunnels() {
        return tunnels;
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