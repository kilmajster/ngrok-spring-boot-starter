package ngrok;

import ngrok.api.NgrokApiClient;
import ngrok.api.model.NgrokTunnel;
import org.springframework.beans.factory.annotation.Autowired;

@NgrokComponent
public class Ngrok {

    private final NgrokApiClient ngrokApiClient;

    @Autowired
    public Ngrok(NgrokApiClient ngrokApiClient) {
        this.ngrokApiClient = ngrokApiClient;
    }

    /**
     * Returns https ngrok tunnel URL or null if tunnel is not running.
     */
    public String getHttpsTunnelUrl() {
        return ngrokApiClient.fetchTunnels()
                .stream()
                .filter(NgrokTunnel::isHttps)
                .findFirst()
                .map(NgrokTunnel::getPublicUrl)
                .orElse(null);
    }

    /**
     * Returns http ngrok tunnel URL or null if tunnel is not running.
     */
    public String getHttpTunnelUrl() {
        return ngrokApiClient.fetchTunnels()
                .stream()
                .filter(NgrokTunnel::isHttp)
                .findFirst()
                .map(NgrokTunnel::getPublicUrl)
                .orElse(null);
    }

    /**
     * @return true if ngrok tunneling is running.
     */
    public boolean isRunning() {
        return ngrokApiClient.isResponding();
    }
}