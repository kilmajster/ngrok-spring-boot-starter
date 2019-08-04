package io.github.createam.ngrok.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ConditionalOnProperty(name = "ngrok.enabled", havingValue = "true")
@Configuration
public class NgrokAsyncConfiguration {

    @Bean("ngrokExecutor")
    public TaskExecutor ngrokAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("ngrok-thread-");
        executor.initialize();

        return executor;
    }
}
