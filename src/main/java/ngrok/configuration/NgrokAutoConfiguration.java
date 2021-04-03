package ngrok.configuration;

import ngrok.NgrokProperties;
import ngrok.NgrokRunner;
import ngrok.api.NgrokApiClient;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import ngrok.os.NgrokSystemCommandExecutor;
import ngrok.util.NgrokDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@ComponentScan(basePackages = "ngrok")
@Configuration
public class NgrokAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(NgrokAutoConfiguration.class);

    @Bean
    @ConditionalOnProperty(name = NgrokProperties.NGROK_ENABLED, havingValue = "true")
    public NgrokRunner ngrokRunner(
            @Autowired NgrokApiClient ngrokApiClient,
            @Autowired NgrokDownloader ngrokDownloader,
            @Autowired NgrokBinaryProvider ngrokBinaryProvider,
            @Autowired NgrokPlatformDetector ngrokPlatformDetector,
            @Autowired NgrokConfigurationProvider ngrokConfigurationProvider,
            @Autowired NgrokSystemCommandExecutor ngrokSystemCommandExecutor,
            @Autowired @Qualifier("ngrokAsyncExecutor") TaskExecutor ngrokExecutor) {
        log.info("Ngrok is enabled.");

        return new NgrokRunner(ngrokApiClient, ngrokBinaryProvider, ngrokConfigurationProvider, ngrokDownloader,
                ngrokPlatformDetector, ngrokSystemCommandExecutor, ngrokExecutor);
    }
}