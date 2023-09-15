package ngrok.runner.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ngrok.TestConstants;
import ngrok.api.NgrokAgentApiClient;
import ngrok.api.model.NgrokTunnel;
import ngrok.api.model.NgrokTunnelsList;
import ngrok.download.NgrokDownloader;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import ngrok.os.NgrokSystemCommandExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.Collections;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseNgrokRunnerMockedConfiguration {

    public static NgrokAgentApiClient mockedNgrokAgentApiClient;
    public static NgrokDownloader mockedNgrokDownloader;
    public static NgrokSystemCommandExecutor mockedNgrokSystemCommandExecutor;
    public static NgrokBinaryProvider mockedNgrokBinaryProvider;
    public static NgrokPlatformDetector mockedNgrokPlatformDetector;

    public abstract NgrokBinaryProvider mockedNgrokBinaryProvider();

    public abstract NgrokPlatformDetector mockedNgrokPlatformDetector();

    @Bean
    @Primary
    public NgrokAgentApiClient mockedNgrokApiClient(@Value("${test.running.port:0}") int runningPort) throws IOException {
        NgrokAgentApiClient ngrokAgentApiClient = mock(NgrokAgentApiClient.class);

        when(ngrokAgentApiClient.getNgrokApiUrl()).thenReturn(TestConstants.TEST_NGROK_API_URL);

        NgrokTunnelsList ngrokTunnelsList = new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(ResourceUtils.getFile(this.getClass().getResource(TestConstants.TEST_NGROK_TUNNELS_LIST_FILE_PATH)), NgrokTunnelsList.class);

        when(ngrokAgentApiClient.listTunnels()).thenReturn(ngrokTunnelsList.getTunnels());

        if (runningPort == 0) {
            when(ngrokAgentApiClient.isResponding())
                    .thenReturn(false)
                    .thenReturn(true);
            when(ngrokAgentApiClient.listTunnels(anyInt())).thenCallRealMethod();
        } else {
            when(ngrokAgentApiClient.isResponding())
                    .thenReturn(true);

            NgrokTunnel tunnel = ngrokTunnelsList.getTunnels().get(0);
            tunnel.getConfig().setAddr("http://localhost:" + runningPort);
            when(ngrokAgentApiClient.listTunnels(runningPort))
                    .thenReturn(Collections.singletonList(tunnel));
        }

        when(ngrokAgentApiClient.startTunnel(anyInt(), anyString(), anyString()))
                .thenReturn(ngrokTunnelsList.getTunnels().get(0));

        mockedNgrokAgentApiClient = ngrokAgentApiClient;

        return ngrokAgentApiClient;
    }

    @Bean
    @Primary
    public NgrokDownloader mockedNgrokDownloader() {
        mockedNgrokDownloader = mock(NgrokDownloader.class);
        return mockedNgrokDownloader;
    }

    @Bean
    @Primary
    public NgrokSystemCommandExecutor mockedNgrokSystemCommandExecutor() {
        mockedNgrokSystemCommandExecutor = mock(NgrokSystemCommandExecutor.class);
        return mockedNgrokSystemCommandExecutor;
    }
}
