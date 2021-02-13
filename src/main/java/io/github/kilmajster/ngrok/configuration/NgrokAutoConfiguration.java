package io.github.kilmajster.ngrok.configuration;

import io.github.kilmajster.ngrok.api.NgrokApiClient;
import io.github.kilmajster.ngrok.util.NgrokDownloader;
import io.github.kilmajster.ngrok.NgrokRunner;
import io.github.kilmajster.ngrok.os.SystemCommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import static io.github.kilmajster.ngrok.NgrokConstants.PROP_NGROK_ENABLED;

@ComponentScan(basePackages = "io.github.kilmajster.ngrok")
@Configuration
public class NgrokAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(NgrokAutoConfiguration.class);

    @Bean
    @ConditionalOnProperty(name = PROP_NGROK_ENABLED, havingValue = "true")
    public NgrokRunner ngrokRunner(
            @Value("${server.port:8080}") String springServerPort,
            @Value("${ngrok.directory:}") String ngrokDirectory,
            @Value("${ngrok.config:}") String ngrokConfigFilePath,
            @Value("${ngrok.command:}") String ngrokCustomCommand,
            @Autowired NgrokApiClient ngrokApiClient,
            @Autowired NgrokDownloader ngrokDownloader,
            @Autowired @Qualifier("ngrokCommandExecutor") SystemCommandExecutor systemCommandExecutor,
            @Autowired @Qualifier("ngrokExecutor") TaskExecutor ngrokExecutor) {
        log.info("Ngrok is enabled.");

        return new NgrokRunner(springServerPort, ngrokDirectory, ngrokConfigFilePath, ngrokCustomCommand,
                ngrokApiClient, ngrokDownloader, systemCommandExecutor, ngrokExecutor);
    }
}