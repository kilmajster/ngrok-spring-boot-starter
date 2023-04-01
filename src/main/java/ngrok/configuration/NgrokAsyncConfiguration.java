package ngrok.configuration;

import ngrok.NgrokComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@NgrokComponent
public class NgrokAsyncConfiguration {

    public static final String NGROK_EXECUTOR_BEAN = "ngrokAsyncExecutor";
    private static final String NGROK_THREAD_PREFIX = "ngrok-thread-";

    @Bean(NGROK_EXECUTOR_BEAN)
    public TaskExecutor ngrokExecutor() {
        return new SimpleAsyncTaskExecutor(NGROK_THREAD_PREFIX);
    }
}