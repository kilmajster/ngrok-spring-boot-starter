package ngrok.runner.config;

import ngrok.TestConstants;
import ngrok.api.NgrokApiClient;
import ngrok.api.model.NgrokTunnel;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import ngrok.os.NgrokSystemCommandExecutor;
import ngrok.download.NgrokDownloader;
import com.fasterxml.jackson.databind.ObjectMapper;
import ngrok.api.model.NgrokTunnelsList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.Collections;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseNgrokRunnerMockedConfiguration {

    public static NgrokApiClient mockedNgrokApiClient;
    public static NgrokDownloader mockedNgrokDownloader;
    public static NgrokSystemCommandExecutor mockedNgrokSystemCommandExecutor;
    public static NgrokBinaryProvider mockedNgrokBinaryProvider;
    public static NgrokPlatformDetector mockedNgrokPlatformDetector;

    public abstract NgrokBinaryProvider mockedNgrokBinaryProvider();

    public abstract NgrokPlatformDetector mockedNgrokPlatformDetector();

    @Bean
    @Primary
    public NgrokApiClient mockedNgrokApiClient(@Value("${test.running.port:0}") int runningPort) throws IOException {
        NgrokApiClient ngrokApiClient = mock(NgrokApiClient.class);

        when(ngrokApiClient.getNgrokApiUrl()).thenReturn(TestConstants.TEST_NGROK_API_URL);

        NgrokTunnelsList ngrokTunnelsList = new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(ResourceUtils.getFile(this.getClass().getResource(TestConstants.TEST_NGROK_TUNNELS_LIST_FILE_PATH)), NgrokTunnelsList.class);

        when(ngrokApiClient.listTunnels()).thenReturn(ngrokTunnelsList.getTunnels());

        if (runningPort == 0) {
            when(ngrokApiClient.isResponding())
                    .thenReturn(false)
                    .thenReturn(true);
            when(ngrokApiClient.listTunnels(anyInt())).thenCallRealMethod();
        } else {
            when(ngrokApiClient.isResponding())
                    .thenReturn(true);

            NgrokTunnel tunnel = ngrokTunnelsList.getTunnels().get(0);
            tunnel.getConfig().setAddr("http://localhost:" + runningPort);
            when(ngrokApiClient.listTunnels(runningPort))
                    .thenReturn(Collections.singletonList(tunnel));
        }

        when(ngrokApiClient.startTunnel(anyInt(), anyString(), anyString()))
                .thenReturn(ngrokTunnelsList.getTunnels().get(0));

        mockedNgrokApiClient = ngrokApiClient;

        return ngrokApiClient;
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
