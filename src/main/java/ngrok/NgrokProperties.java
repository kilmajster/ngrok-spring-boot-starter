package ngrok;

/**
 * For details see <a href="https://github.com/kilmajster/ngrok-spring-boot-starter">docs</a>. 😎
 */
public interface NgrokProperties {
    // properties keys
    /**
     * Property key for enabling ngrok. eg. <code></>ngrok.enabled=true</code>.
     */
    String NGROK_ENABLED = "ngrok.enabled";

    /**
     * Property key for providing custom configuration. eg. <code></>ngrok.config=/home/user/example-directory/custom-config.yml</code>
     */
    String NGROK_CONFIG = "ngrok.config";

    /**
     * Property key for providing ngrok custom execution parameters. eg. <code></>ngrok.command=http 3000 -config /root/ngrok.yml</code>
     */
    String NGROK_COMMAND = "ngrok.command";

    /**
     * Property key for ngrok host.
     */
    String NGROK_HOST = "ngrok.host";

    /**
     * Property key for ngrok port.
     */
    String NGROK_PORT = "ngrok.port";

    /**
     * Property key for ngrok custom directory. eg <code>ngrok.directory=/some-custom/ngrok/location/.ngrok2</code>
     */
    String NGROK_DIRECTORY = "ngrok.directory";

    /**
     * Property key for time of waiting for ngrok tunneling startup in milliseconds.
     */
    String NGROK_WAIT_FOR_STARTUP = "ngrok.waitForStartup.millis";

    /**
     * Property key for Windows 32 binary url.
     */
    String NGROK_BINARY_WINDOWS_32 = "ngrok.binary.windows32";

    /**
     * Property key for Linux 32 binary url.
     */
    String NGROK_BINARY_LINUX_32 = "ngrok.binary.linux32";

    /**
     * Property key for Mac OS X 32 binary url.
     */
    String NGROK_BINARY_OSX_32 = "ngrok.binary.osx32";

    /**
     * Property key for Windows 64 binary url.
     */
    String NGROK_BINARY_WINDOWS_64 = "ngrok.binary.windows";

    /**
     * Property key for Linux 64 binary url.
     */
    String NGROK_BINARY_LINUX_64 = "ngrok.binary.linux";

    /**
     * Property key for Mac OS X 64 binary url.
     */
    String NGROK_BINARY_OSX_64 = "ngrok.binary.osx";

    /**
     * Property key for custom binary url. Could be used as fallback location of zipped ngrok binary.
     */
    String NGROK_BINARY_CUSTOM = "ngrok.binary.custom";

    /**
     * Property key for Springs server port. See <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#server.port">docs</a>.
     */
    String SPRING_SERVER_PORT = "server.port";

    // defaults
    String NGROK_HOST_DEFAULT = "http://127.0.0.1";
    int NGROK_PORT_DEFAULT = 4040;
    int NGROK_WAIT_FOR_STARTUP_DEFAULT = 3000;
    String NGROK_BINARY_WINDOWS_32_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip";
    String NGROK_BINARY_LINUX_32_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip";
    String NGROK_BINARY_OSX_32_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip";
    String NGROK_BINARY_WINDOWS_64_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-amd64.zip";
    String NGROK_BINARY_LINUX_64_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip";
    String NGROK_BINARY_OSX_64_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-amd64.zip";
    int SPRING_SERVER_PORT_DEFAULT = 8080;
}