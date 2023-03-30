package ngrok.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ngrok.NgrokComponent;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@NgrokComponent
@ConfigurationProperties(prefix = "ngrok")
public class NgrokConfiguration {

    public static final String NGROK_ENABLED = "ngrok.enabled";
    public static final String NGROK_CONFIG_FILES_SEPARATOR = ";";
    public static final String NGROK_CONFIG_FILE_NAME = "ngrok.yml";
    public static final String NGROK_DIRECTORY_NAME = ".ngrok3";
    public static final String NGROK_LEGACY_DIRECTORY_NAME = ".ngrok2";

    /**
     * Enable ngrok
     */
    private boolean enabled;

    /**
     * If true, ngrok v2 will be used.
     */
    private boolean legacy;

    /**
     * If ngrok binary is present in PATH, use it.
     */
    private boolean useFromPath = true;

    /**
     * Property for personal Ngrok authToken, it can be found here - https://dashboard.ngrok.com/get-started/your-authtoken
     */
    private String authToken;

    /**
     * Custom configuration path e.g.: '/home/user/example-directory/custom-config.yml'
     * For multiple config files use ; as a path separator
     */
    private String config;

    /**
     * Provide custom ngrok execution parameters. e.g.: 'http 3000 -config /root/ngrok.yml'
     */
    private String command;

    /**
     * ngrok host
     */
    private String host = "http://127.0.0.1";

    /**
     * ngrok web-interface port
     */
    private int port = 4040;

    /**
     * Custom ngrok directory e.g.: '/some-custom/ngrok/location/.ngrok2'
     */
    private String directory;

    /**
     * Delay in millis to wait until ngrok started
     */
    private long startupDelay = 3000L;

    /**
     * Url of custom Ngrok binary archive
     */
    private String customArchiveUrl;

}
