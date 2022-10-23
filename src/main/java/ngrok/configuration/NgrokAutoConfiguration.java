package ngrok.configuration;

import lombok.extern.slf4j.Slf4j;
import ngrok.NgrokComponent;
import ngrok.NgrokRunner;
import ngrok.api.NgrokApiClient;
import ngrok.download.NgrokArchiveUrlProvider;
import ngrok.download.NgrokLegacyV2ArchiveUrls;
import ngrok.download.NgrokV3ArchiveUrls;
import ngrok.os.NgrokBinaryProvider;
import ngrok.os.NgrokPlatformDetector;
import ngrok.os.NgrokSystemCommandExecutor;
import ngrok.download.NgrokDownloader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;

@Slf4j
@ComponentScan(basePackages = "ngrok")
@NgrokComponent
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
            @Qualifier("ngrokAsyncExecutor") TaskExecutor ngrokExecutor,
            @Value("${spring.application.name:springboot}") String applicationName
    ) {
        log.info("Ngrok is enabled.");

        return new NgrokRunner(applicationEventPublisher, ngrokConfiguration, ngrokApiClient, ngrokBinaryProvider,
                ngrokConfigurationProvider, ngrokDownloader, ngrokPlatformDetector, ngrokSystemCommandExecutor,
                ngrokExecutor, applicationName);
    }

    @Bean
    public NgrokArchiveUrlProvider ngrokArchiveUrlProvider(NgrokConfiguration ngrokConfiguration) {
        return ngrokConfiguration.isLegacy() ? new NgrokLegacyV2ArchiveUrls() : new NgrokV3ArchiveUrls();
    }
}
