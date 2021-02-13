package io.github.kilmajster.ngrok.configuration;

import io.github.kilmajster.ngrok.NgrokComponent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import static io.github.kilmajster.ngrok.NgrokConstants.PROP_NGROK_ENABLED;

@NgrokComponent
public class NgrokAsyncConfiguration {

  private static final String NGROK_THREAD_PREFIX = "ngrok-thread-";

  @Bean("ngrokExecutor")
  public TaskExecutor ngrokAsyncExecutor() {
    return new SimpleAsyncTaskExecutor(NGROK_THREAD_PREFIX);
  }
}
