package io.github.createam.ngrok.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TunnelsList {

   private List<Tunnel> tunnels;

    public TunnelsList() {
        this.tunnels = new ArrayList<>();
    }

    public List<Tunnel> getTunnels() {
        if(tunnels == null) {
            return Collections.emptyList();
        }
        return tunnels;
    }

    public void setTunnels(List<Tunnel> tunnels) {
        this.tunnels = tunnels;
    }
}
