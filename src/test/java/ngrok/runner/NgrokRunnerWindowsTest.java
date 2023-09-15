package ngrok.runner;


import ngrok.runner.config.NgrokRunnerWindowsTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static ngrok.TestConstants.*;
import static ngrok.runner.config.NgrokRunnerWindowsTestConfiguration.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles({
        TEST_NGROK_PROFILE,
        TEST_NGROK_PROFILE_WINDOWS
})
@EnableAutoConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = TEST_NGROK_PROP_ENABLED,
        classes = NgrokRunnerWindowsTestConfiguration.class)
public class NgrokRunnerWindowsTest extends BaseNgrokRunnerIntegrationTest {

    @Test
    public void shouldStartNgrok_whenNgrokIsEnabled_onWindows() {
        verify(mockedNgrokBinaryProvider).isNgrokBinaryPresent();
        verify(mockedNgrokDownloader).downloadAndExtractNgrokTo(eq(TEST_NGROK_WINDOWS_DEFAULT_DIR));
        verify(mockedNgrokPlatformDetector).isUnix();
        verify(mockedNgrokBinaryProvider).getNgrokBinaryFilePath();
        verify(mockedNgrokSystemCommandExecutor).execute(eq(TEST_NGROK_WINDOWS_START_COMMAND));
        verify(mockedNgrokAgentApiClient, times(2)).isResponding();
    }
}