package ngrok.runner;

import ngrok.runner.config.NgrokRunnerUnixTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static ngrok.TestConstants.*;
import static ngrok.runner.config.BaseNgrokRunnerMockedConfiguration.*;
import static org.mockito.Mockito.*;

@ActiveProfiles({
        TEST_NGROK_PROFILE,
        TEST_NGROK_PROFILE_UNIX
})
@EnableAutoConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                TEST_NGROK_PROP_ENABLED,
                "test.running.port=" + TEST_PORT_1,
        },
        classes = NgrokRunnerUnixTestConfiguration.class)
public class NgrokRunnerRunningTest extends BaseNgrokRunnerIntegrationTest {

    @Test
    public void shouldNotStartNgrok_whenNgrokIsAlreadyRunning() {
        verify(mockedNgrokBinaryProvider, never()).isNgrokBinaryPresent();
        verify(mockedNgrokDownloader, never()).downloadAndExtractNgrokTo(anyString());
        verify(mockedNgrokPlatformDetector, never()).isUnix();
        verify(mockedNgrokSystemCommandExecutor, never()).execute(anyString());
        verify(mockedNgrokBinaryProvider, never()).getNgrokBinaryFilePath();
        verify(mockedNgrokApiClient, times(1)).isResponding();
    }
}
