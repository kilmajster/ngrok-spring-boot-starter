package ngrok.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NgrokTunnelsList implements Serializable {

    private List<NgrokTunnel> tunnels;

}
