package com.createam.ngrok.autoconfiguration;

import com.createam.ngrok.experimental.embedded.NgrokConfiguration;
import com.createam.ngrok.experimental.embedded.NgrokEmbeddedRunner;
import com.createam.ngrok.experimental.embedded.NgrokLinuxConfiguration;
import com.createam.ngrok.experimental.embedded.NgrokWindowsConfiguration;
import com.createam.ngrok.experimental.embedded.SystemInfo;
import com.createam.ngrok.experimental.embedded.SystemType;
import com.createam.ngrok.service.NgrokLocalRunner;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ComponentScan(basePackages = "com.createam.ngrok")
public class NgrokAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(NgrokAutoConfiguration.class);

    @Bean
    @ConditionalOnProperty(name = "ngrok.path")
    public NgrokLocalRunner runLocally(final @Value("${ngrok.path}") String ngrokPath) {
        return new NgrokLocalRunner(ngrokPath);
    }

    @Bean
    @ConditionalOnProperty(name = "ngrok.experimental.embedded.enabled")
    public NgrokConfiguration osConfig(final @Value("${ngrok.experimental.embedded.path}") String ngrokCachePath) {
        return SystemInfo.checkOS() == SystemType.WINDOWS ? new NgrokWindowsConfiguration(ngrokCachePath) : new NgrokLinuxConfiguration(ngrokCachePath);
    }
}
