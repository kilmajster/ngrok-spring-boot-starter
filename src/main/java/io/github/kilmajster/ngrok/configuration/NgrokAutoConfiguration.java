package io.github.kilmajster.ngrok.configuration;

import io.github.kilmajster.ngrok.NgrokConstants;
import io.github.kilmajster.ngrok.os.NgrokBinaryProvider;
import io.github.kilmajster.ngrok.NgrokRunner;
import io.github.kilmajster.ngrok.api.NgrokApiClient;
import io.github.kilmajster.ngrok.os.NgrokPlatformDetector;
import io.github.kilmajster.ngrok.os.NgrokSystemCommandExecutor;
import io.github.kilmajster.ngrok.util.NgrokDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@ComponentScan(basePackages = "io.github.kilmajster.ngrok")
@Configuration
public class NgrokAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(NgrokAutoConfiguration.class);

    @Bean
    @ConditionalOnProperty(name = NgrokConstants.PROP_NGROK_ENABLED, havingValue = "true")
    public NgrokRunner ngrokRunner(
            @Autowired NgrokApiClient ngrokApiClient,
            @Autowired NgrokDownloader ngrokDownloader,
            @Autowired NgrokBinaryProvider ngrokBinaryProvider,
            @Autowired NgrokPlatformDetector ngrokPlatformDetector,
            @Autowired NgrokSystemCommandExecutor ngrokSystemCommandExecutor,
            @Autowired @Qualifier("ngrokAsyncExecutor") TaskExecutor ngrokExecutor) {
        log.info("Ngrok is enabled.");

        return new NgrokRunner(
                ngrokApiClient, ngrokBinaryProvider, ngrokDownloader,
                ngrokPlatformDetector, ngrokSystemCommandExecutor, ngrokExecutor);
    }
}