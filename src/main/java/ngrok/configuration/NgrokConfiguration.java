package ngrok.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ngrok.NgrokComponent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
@NoArgsConstructor
@NgrokComponent
@ConfigurationProperties(prefix = "ngrok")
public class NgrokConfiguration {

    public static final String NGROK_ENABLED = "ngrok.enabled";
    public static final String NGROK_CONFIG_FILES_SEPARATOR = ";";
    public static final String NGROK_CONFIG_FILE_NAME = "ngrok.yml";

    /**
     * Enable ngrok
     */
    private Boolean enabled;

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
    private Integer port = 4040;

    /**
     * Custom ngrok directory e.g.: '/some-custom/ngrok/location/.ngrok2'
     */
    private String directory;

    /**
     * Delay in millis to wait until ngrok started
     */
    private Long startupDelay = 3000L;

    /**
     * Set the binary download URL
     */
    @NestedConfigurationProperty
    private NgrokBinary binary = new NgrokBinary();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NgrokBinary {

        /**
         * Windows 64 bit binary
         */
        private String windows = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-amd64.zip";

        /**
         * Linux 64 bit binary
         */
        private String linux = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip";

        /**
         * OSX 64 bit binary
         */
        private String osx = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-amd64.zip";

        /**
         * Windows 32 bit binary
         */
        private String windows32 = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip";

        /**
         * Linux 32 bit binary
         */
        private String linux32 = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip";

        /**
         * OSX 32 bit binary
         */
        private String osx32 = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip";

        /**
         * Custom url to download ngrok binary from (independent from system)
         */
        private String custom;

    }

}
