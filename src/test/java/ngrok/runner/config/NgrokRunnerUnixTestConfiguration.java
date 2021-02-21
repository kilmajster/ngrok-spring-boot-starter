package ngrok.runner.config;

import ngrok.TestConstants;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import org.junit.AfterClass;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.when;

@Configuration
@Profile(TestConstants.TEST_NGROK_PROFILE_UNIX)
public class NgrokRunnerUnixTestConfiguration extends BaseNgrokRunnerMockedConfiguration {

    @Bean
    @Primary
    @Override
    public NgrokBinaryProvider mockedNgrokBinaryProvider() {
        NgrokBinaryProvider ngrokBinaryProvider = Mockito.mock(NgrokBinaryProvider.class);

        when(ngrokBinaryProvider.isNgrokBinaryPresent()).thenReturn(false);
        when(ngrokBinaryProvider.getNgrokDirectoryOrDefault()).thenReturn(TestConstants.TEST_NGROK_UNIX_DEFAULT_DIR);
        when(ngrokBinaryProvider.getNgrokBinaryFilePath()).thenReturn(TestConstants.TEST_NGROK_UNIX_BINARY_PATH);

        mockedNgrokBinaryProvider = ngrokBinaryProvider;

        return ngrokBinaryProvider;
    }

    @Bean
    @Primary
    @Override
    public NgrokPlatformDetector mockedNgrokPlatformDetector() {
        NgrokPlatformDetector ngrokPlatformDetector = Mockito.mock(NgrokPlatformDetector.class);

        when(ngrokPlatformDetector.isUnix()).thenReturn(true);

        mockedNgrokPlatformDetector = ngrokPlatformDetector;

        return ngrokPlatformDetector;
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        Thread.sleep(1000); // because spring context initialized in thic class needs to be shutted down gracefully
    }

}
