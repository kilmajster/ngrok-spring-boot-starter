package io.github.kilmajster.ngrok.runner.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kilmajster.ngrok.api.NgrokApiClient;
import io.github.kilmajster.ngrok.api.model.NgrokTunnelsList;
import io.github.kilmajster.ngrok.os.NgrokBinaryProvider;
import io.github.kilmajster.ngrok.os.NgrokPlatformDetector;
import io.github.kilmajster.ngrok.os.NgrokSystemCommandExecutor;
import io.github.kilmajster.ngrok.util.NgrokDownloader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static io.github.kilmajster.ngrok.TestConstants.TEST_NGROK_API_URL;
import static io.github.kilmajster.ngrok.TestConstants.TEST_NGROK_TUNNELS_FILE_PATH;
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
    public NgrokApiClient mockedNgrokApiClient() throws IOException {
        NgrokApiClient ngrokApiClient = mock(NgrokApiClient.class);

        when(ngrokApiClient.isResponding())
                .thenReturn(false)
                .thenReturn(true);

        when(ngrokApiClient.getNgrokApiUrl()).thenReturn(TEST_NGROK_API_URL);

        NgrokTunnelsList ngrokTunnelsList = new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(ResourceUtils.getFile(this.getClass().getResource(TEST_NGROK_TUNNELS_FILE_PATH)), NgrokTunnelsList.class);

        when(ngrokApiClient.fetchTunnels()).thenReturn(ngrokTunnelsList.getTunnels());

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
