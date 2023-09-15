package ngrok.runner;

import ngrok.runner.config.NgrokRunnerUnixTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static ngrok.TestConstants.*;
import static ngrok.runner.config.BaseNgrokRunnerMockedConfiguration.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ActiveProfiles({
        TEST_NGROK_PROFILE,
        TEST_NGROK_PROFILE_UNIX
})
@EnableAutoConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = NgrokRunnerUnixTestConfiguration.class,
        properties = {
                TEST_NGROK_PROP_ENABLED,
                TEST_NGROK_PROP_CUSTOM_CONFIG,
        })
public class NgrokRunnerWithConfigFileTest extends BaseNgrokRunnerIntegrationTest {

    @Test
    public void shouldStartNgrok_withConfigFile_onUnix() {
        verify(mockedNgrokBinaryProvider).isNgrokBinaryPresent();
        verify(mockedNgrokDownloader).downloadAndExtractNgrokTo(eq(TEST_NGROK_UNIX_DEFAULT_DIR));
        verify(mockedNgrokPlatformDetector).isUnix();
        verify(mockedNgrokSystemCommandExecutor).execute(eq(TEST_NGROK_UNIX_CHMOD_COMMAND));
        verify(mockedNgrokSystemCommandExecutor).execute(eq(TEST_NGROK_UNIX_START_COMMAND_WITH_CONFIG));
        verify(mockedNgrokBinaryProvider, times(2)).getNgrokBinaryFilePath();
        verify(mockedNgrokAgentApiClient, times(2)).isResponding();
    }
}