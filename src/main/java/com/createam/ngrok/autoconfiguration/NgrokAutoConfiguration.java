package com.createam.ngrok.autoconfiguration;

import com.createam.ngrok.service.NgrokRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.createam.ngrok")
public class NgrokAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "ngrok.path")
    public NgrokRunner runLocally(final @Value("${ngrok.path}") String ngrokPath) {
        return new NgrokRunner(ngrokPath);
    }
}
