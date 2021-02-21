package ngrok.configuration;

import ngrok.NgrokComponent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.context.ActiveProfiles;

import static ngrok.TestConstants.TEST_NGROK_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(TEST_NGROK_PROFILE)
@SpringBootTest(
        classes = NgrokAutoConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class NgrokAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(NgrokAutoConfiguration.class));

    @Test
    public void whenNgrokIsNotEnabled_shouldNotIncludeAnyNgrokBeans() {
        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(NgrokComponent.class));
    }
}