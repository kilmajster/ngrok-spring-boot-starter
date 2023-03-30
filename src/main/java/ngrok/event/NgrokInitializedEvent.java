package ngrok.event;

import lombok.Getter;
import ngrok.api.model.NgrokTunnel;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class NgrokInitializedEvent extends ApplicationEvent {

    private final List<NgrokTunnel> tunnels;

    public NgrokInitializedEvent(Object source, List<NgrokTunnel> tunnels) {
        super(source);
        this.tunnels = tunnels;
    }

}
