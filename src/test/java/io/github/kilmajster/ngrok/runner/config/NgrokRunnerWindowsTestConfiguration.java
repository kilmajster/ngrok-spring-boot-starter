package io.github.kilmajster.ngrok.runner.config;

import io.github.kilmajster.ngrok.os.NgrokBinaryProvider;
import io.github.kilmajster.ngrok.os.NgrokPlatformDetector;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static io.github.kilmajster.ngrok.TestConstants.*;
import static org.mockito.Mockito.when;

@Configuration
@Profile(TEST_NGROK_PROFILE_WINDOWS)
public class NgrokRunnerWindowsTestConfiguration extends BaseNgrokRunnerMockedConfiguration {

    @Bean
    @Primary
    @Override
    public NgrokBinaryProvider mockedNgrokBinaryProvider() {
        NgrokBinaryProvider ngrokBinaryProvider = Mockito.mock(NgrokBinaryProvider.class);

        when(ngrokBinaryProvider.isNgrokBinaryPresent()).thenReturn(false);
        when(ngrokBinaryProvider.getNgrokDirectoryOrDefault()).thenReturn(TEST_NGROK_WINDOWS_DEFAULT_DIR);
        when(ngrokBinaryProvider.getNgrokBinaryFilePath()).thenReturn(TEST_NGROK_WINDOWS_BINARY_PATH);

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