package io.github.createam.ngrok.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TunnelsList implements Serializable {

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
