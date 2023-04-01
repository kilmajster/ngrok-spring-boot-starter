package ngrok.configuration;

import lombok.extern.slf4j.Slf4j;
import ngrok.NgrokComponent;
import ngrok.download.NgrokArchiveUrlProvider;
import ngrok.download.NgrokLegacyV2ArchiveUrls;
import ngrok.download.NgrokV3ArchiveUrls;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@ComponentScan(basePackages = "ngrok")
@NgrokComponent
public class NgrokAutoConfiguration {

    @Bean
    public NgrokArchiveUrlProvider ngrokArchiveUrlProvider(NgrokConfiguration ngrokConfiguration) {
        log.info("Ngrok is enabled.");
        return ngrokConfiguration.isLegacy() ? new NgrokLegacyV2ArchiveUrls() : new NgrokV3ArchiveUrls();
    }
}
