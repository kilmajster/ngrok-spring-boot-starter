package io.github.createam.ngrok.config;

import io.github.createam.ngrok.NgrokApiClient;
import io.github.createam.ngrok.NgrokDownloader;
import io.github.createam.ngrok.NgrokRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = NgrokAutoConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NgrokAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(NgrokAutoConfiguration.class));

    @Test
    public void shouldNotCreateNgrokRunnerWhenPropertyIsDisabled() {
        this.contextRunner.withPropertyValues("ngrok.enabled=false");

        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(NgrokRunner.class));
    }

    @Test
    public void ngrokRunnerShouldBeNotVisibleWhenPropertyIsDisabled() {
        this.contextRunner.withPropertyValues("ngrok.enabled=false");

        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(NgrokRunner.class));
    }

    @Test
    public void ngrokDownloaderShouldBeNotVisibleWhenPropertyIsDisabled() {
        this.contextRunner.withPropertyValues("ngrok.enabled=false");

        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(NgrokDownloader.class));
    }

    @Test
    public void ngrokApiClientShouldBeNotVisibleWhenPropertyIsDisabled() {
        this.contextRunner.withPropertyValues("ngrok.enabled=false");

        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(NgrokApiClient.class));
    }
}