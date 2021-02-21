package ngrok.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NgrokTunnelsList implements Serializable {

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
}