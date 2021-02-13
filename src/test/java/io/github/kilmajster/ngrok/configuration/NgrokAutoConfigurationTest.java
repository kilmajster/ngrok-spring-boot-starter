package io.github.kilmajster.ngrok.configuration;

import io.github.kilmajster.ngrok.NgrokComponent;
import io.github.kilmajster.ngrok.NgrokRunner;
import io.github.kilmajster.ngrok.util.NgrokDownloader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = NgrokAutoConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RunWith(SpringRunner.class)
public class NgrokAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(NgrokAutoConfiguration.class));

    @Test
    public void ngrokAsyncConfigurationShouldBeNotVisibleWhenPropertyIsDisabled() {
        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(NgrokAsyncConfiguration.class));
    }

    @Test
    public void ngrokRunnerShouldBeNotVisibleWhenPropertyIsDisabled() {
        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(NgrokRunner.class));
    }

    @Test
    public void ngrokDownloaderShouldBeNotVisibleWhenPropertyIsDisabled() {
        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(NgrokDownloader.class));
    }

    @Test
    public void whenNgrokIsNotEnabled_shouldNotIncludeAnyNgrokBeans() {
        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(NgrokComponent.class));
    }
}