package ngrok.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@ConditionalOnProperty(name = NgrokConfiguration.NGROK_ENABLED, havingValue = "true")
@Configuration
@ConfigurationProperties(prefix = "ngrok")
public class NgrokConfiguration {

    public static final String NGROK_ENABLED = "ngrok.enabled";

    private Boolean enabled;

    private String configPath;

    private String command;

    private String host = "http://127.0.0.1";

    private Integer port = 4040;

    private String directory;

    private Long startupDelay = 3000L;

    private NgrokBinary binary;

    @Getter
    @Setter
    @NoArgsConstructor
    private static class NgrokBinary {

        private String windows = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-amd64.zip";

        private String linux = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip";

        private String osx = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-amd64.zip";

        private String windows32 = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip";;

        private String linux32 = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip";

        private String osx32 = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip";

        private String custom;

    }

}
