package io.github.createam.ngrok.configuration;

import io.github.createam.ngrok.NgrokRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "io.github.createam.ngrok")
public class NgrokAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(NgrokAutoConfiguration.class);

    @Bean
    @ConditionalOnProperty(name = "ngrok.enabled", havingValue = "true")
    public NgrokRunner ngrokRunner() {
        log.info("Ngrok is enabled.");

        return new NgrokRunner();
    }
}