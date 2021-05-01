package ngrok.runner;

import ngrok.NgrokInitializedEvent;
import ngrok.runner.config.NgrokRunnerUnixTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import static ngrok.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({
        TEST_NGROK_PROFILE,
        TEST_NGROK_PROFILE_UNIX
})
@EnableAutoConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = NgrokRunnerUnixTestConfiguration.class,
        properties = {
                TEST_NGROK_PROP_ENABLED,
        })
public class NgrokRunnerEventTest extends BaseNgrokRunnerIntegrationTest {

    @TestConfiguration
    public static class Config {

        private static NgrokInitializedEvent event;

        @Component
        public static class CustomSpringEventListener implements ApplicationListener<NgrokInitializedEvent> {
            @Override
            public void onApplicationEvent(NgrokInitializedEvent event) {
                Config.event = event;
            }
        }
    }

    @Test
    public void shouldReceiveEvent() {
        assertThat(Config.event).isNotNull();
        assertThat(Config.event.getTunnels()).hasSize(2);
    }
}
