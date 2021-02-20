package io.github.kilmajster.ngrok;

public interface NgrokProperties {
    String NGROK_ENABLED = "ngrok.enabled";
    String NGROK_HOST = "ngrok.host";
    String NGROK_HOST_DEFAULT = "http://127.0.0.1";
    String NGROK_PORT = "ngrok.port";
    int NGROK_PORT_DEFAULT = 4040;
    String NGROK_DIRECTORY ="ngrok.directory";
    String NGROK_WAIT_FOR_STARTUP ="ngrok.waitForStartup.millis";
    int NGROK_WAIT_FOR_STARTUP_DEFAULT = 3000;
    String NGROK_BINARY_WINDOWS_32 = "ngrok.binary.windows32";
    String NGROK_BINARY_WINDOWS_32_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip";
    String NGROK_BINARY_LINUX_32 = "ngrok.binary.linux32";
    String NGROK_BINARY_LINUX_32_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip";
    String NGROK_BINARY_OSX_32 = "ngrok.binary.osx32";
    String NGROK_BINARY_OSX_32_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip";
    String NGROK_BINARY_WINDOWS_64 = "ngrok.binary.windows";
    String NGROK_BINARY_WINDOWS_64_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-amd64.zip";
    String NGROK_BINARY_LINUX_64 = "ngrok.binary.linux";
    String NGROK_BINARY_LINUX_64_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip";
    String NGROK_BINARY_OSX_64 = "ngrok.binary.osx";
    String NGROK_BINARY_OSX_64_DEFAULT = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-amd64.zip";
    String NGROK_BINARY_CUSTOM = "ngrok.binary.custom";
    String SPRING_SERVER_PORT = "server.port";
    int SPRING_SERVER_PORT_DEFAULT = 8080;
    String NGROK_CONFIG = "ngrok.config";
    String NGROK_COMMAND = "ngrok.command";
}