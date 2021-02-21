package ngrok.runner.config;

import ngrok.TestConstants;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.when;

@Configuration
@Profile(TestConstants.TEST_NGROK_PROFILE_WINDOWS)
public class NgrokRunnerWindowsTestConfiguration extends BaseNgrokRunnerMockedConfiguration {

    @Bean
    @Primary
    @Override
    public NgrokBinaryProvider mockedNgrokBinaryProvider() {
        NgrokBinaryProvider ngrokBinaryProvider = Mockito.mock(NgrokBinaryProvider.class);

        when(ngrokBinaryProvider.isNgrokBinaryPresent()).thenReturn(false);
        when(ngrokBinaryProvider.getNgrokDirectoryOrDefault()).thenReturn(TestConstants.TEST_NGROK_WINDOWS_DEFAULT_DIR);
        when(ngrokBinaryProvider.getNgrokBinaryFilePath()).thenReturn(TestConstants.TEST_NGROK_WINDOWS_BINARY_PATH);

        mockedNgrokBinaryProvider = ngrokBinaryProvider;

        return ngrokBinaryProvider;
    }

    @Bean
    @Primary
    @Override
    public NgrokPlatformDetector mockedNgrokPlatformDetector() {
        NgrokPlatformDetector ngrokPlatformDetector = Mockito.mock(NgrokPlatformDetector.class);

        when(ngrokPlatformDetector.isUnix()).thenReturn(false);

        mockedNgrokPlatformDetector = ngrokPlatformDetector;

        return ngrokPlatformDetector;
    }
}