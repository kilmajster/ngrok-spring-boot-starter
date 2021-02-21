package ngrok.configuration;

import ngrok.NgrokComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@NgrokComponent
public class NgrokAsyncConfiguration {

    private static final String NGROK_THREAD_PREFIX = "ngrok-thread-";

    @Bean("ngrokAsyncExecutor")
    public TaskExecutor ngrokExecutor() {
        return new SimpleAsyncTaskExecutor(NGROK_THREAD_PREFIX);
    }
}