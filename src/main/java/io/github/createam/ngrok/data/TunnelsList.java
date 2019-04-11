package io.github.createam.ngrok.data;

import java.util.ArrayList;
import java.util.List;

public class TunnelsList {

    private List<Tunnel> tunnels;

    public TunnelsList() {
        this.tunnels = new ArrayList<>();
    }

    public List<Tunnel> getTunnels() {
        return tunnels;
    }

    public void setTunnels(List<Tunnel> tunnels) {
        this.tunnels = tunnels;
    }
}
