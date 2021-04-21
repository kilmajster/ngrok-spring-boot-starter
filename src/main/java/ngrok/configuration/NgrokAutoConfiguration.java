package ngrok.configuration;

import lombok.extern.slf4j.Slf4j;
import ngrok.NgrokRunner;
import ngrok.api.NgrokApiClient;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import ngrok.os.NgrokSystemCommandExecutor;
import ngrok.util.NgrokDownloader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Slf4j
@ComponentScan(basePackages = "ngrok")
@Configuration
@ConditionalOnBean(NgrokConfiguration.class)
public class NgrokAutoConfiguration {

    @Bean
    public NgrokRunner ngrokRunner(
            ApplicationEventPublisher applicationEventPublisher,
            NgrokConfiguration ngrokConfiguration,
            NgrokApiClient ngrokApiClient,
            NgrokDownloader ngrokDownloader,
            NgrokBinaryProvider ngrokBinaryProvider,
            NgrokPlatformDetector ngrokPlatformDetector,
            NgrokConfigurationProvider ngrokConfigurationProvider,
            NgrokSystemCommandExecutor ngrokSystemCommandExecutor,
            @Qualifier("ngrokAsyncExecutor") TaskExecutor ngrokExecutor) {
        log.info("Ngrok is enabled.");

        return new NgrokRunner(applicationEventPublisher, ngrokConfiguration, ngrokApiClient, ngrokBinaryProvider,
                ngrokConfigurationProvider, ngrokDownloader, ngrokPlatformDetector, ngrokSystemCommandExecutor,
                ngrokExecutor);
    }
}
