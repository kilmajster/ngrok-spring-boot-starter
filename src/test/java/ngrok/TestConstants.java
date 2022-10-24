package ngrok;

import ngrok.api.NgrokApiClient;
import ngrok.api.model.NgrokTunnel;
import ngrok.configuration.NgrokConfiguration;

import java.util.HashMap;

public interface TestConstants {
    int TEST_PORT_1 = 8080;
    int TEST_PORT_2 = 8081;

    String TEST_NGROK_PROP_ENABLED = NgrokConfiguration.NGROK_ENABLED + "=true";
    String TEST_NGROK_PROP_CUSTOM_CONFIG = "ngrok.config=/ngrok-config.yml";
    String TEST_NGROK_PROP_CUSTOM_COMMAND = "ngrok.command=http " + TEST_PORT_2;
    String TEST_SPRING_PROP_SERVER_PORT = "server.port=" + TEST_PORT_2;

    String TEST_NGROK_PROFILE = "test";
    String TEST_NGROK_PROFILE_WINDOWS = "test-windows";
    String TEST_NGROK_PROFILE_UNIX = "test-unix";

    String TEST_NGROK_API_URL = "http://mocked-ngrok-api-url:1234";
    String TEST_NGROK_TUNNELS_LIST_FILE_PATH = "/tunnels-list.json";
    String TEST_NGROK_SINGLE_TUNNEL_FILE_PATH = "/single-tunnel.json";

    String TEST_NGROK_WINDOWS_DEFAULT_DIR = "C:\\Users\\user\\.ngrok3";
    String TEST_NGROK_WINDOWS_BINARY_PATH = "C:\\Users\\user\\.ngrok3\\ngrok.exe";
    String TEST_NGROK_WINDOWS_START_COMMAND = TEST_NGROK_WINDOWS_BINARY_PATH + " http " + TEST_PORT_1;

    String TEST_NGROK_UNIX_DEFAULT_DIR = "/home/user/.ngrok3";
    String TEST_NGROK_UNIX_BINARY_PATH = "/home/user/.ngrok3/ngrok";
    String TEST_NGROK_UNIX_CHMOD_COMMAND = "chmod +x " + TEST_NGROK_UNIX_BINARY_PATH;
    String TEST_NGROK_UNIX_START_COMMAND = TEST_NGROK_UNIX_BINARY_PATH + " http " + TEST_PORT_1;
    String TEST_NGROK_UNIX_START_COMMAND_WITH_CONFIG = TEST_NGROK_UNIX_BINARY_PATH + " http -config /ngrok-config.yml " + TEST_PORT_1;
    String TEST_NGROK_UNIX_START_CUSTOM_COMMAND = TEST_NGROK_UNIX_BINARY_PATH + " http " + TEST_PORT_2;

    String TEST_INVALID_TUNNEL_NAME = "not-existing-tunnel-name";

    NgrokTunnel.NgrokTunnelMetrics TEST_NGROK_TUNNEL_METRICS = NgrokTunnel.NgrokTunnelMetrics.builder()
            .conns(new HashMap<String, Double>() {{
                put("count", 2d);
                put("gauge", 0d);
                put("rate1", 0.0010497099617378827);
                put("rate5", 0.003338310672192081);
                put("rate15", 0.001764651863881216);
                put("p50", 2086075.5);
                put("p90", 2205836d);
                put("p95", 2205836d);
                put("p99", 2205836d);
            }}).http(new HashMap<String, Double>() {{
                put("count", 2d);
                put("rate1", 0.0010497099617378827);
                put("rate5", 0.003338310672192081);
                put("rate15", 0.001764651863881216);
                put("p50", 1005547.5);
                put("p90", 1205297d);
                put("p95", 1205297d);
                put("p99", 1205297d);
            }}).build();

    String TEST_NGROK_TUNNEL_HTTP_NAME = "test-http-tunnel-name";
    String TEST_NGROK_TUNNEL_HTTP_URI = NgrokApiClient.URI_NGROK_API_TUNNELS + "/test-http-tunnel-name";
    String TEST_NGROK_TUNNEL_HTTP_PUBLIC_URL = "http://12345678-not-existing.ngrok.io";
    String TEST_NGROK_TUNNEL_HTTP_PROTO = "http";
    String TEST_NGROK_TUNNEL_CONFIG_ADDR = "http://localhost:8080";
    boolean TEST_NGROK_TUNNEL_CONFIG_INSPECT = true;
    int TEST_NGROK_TUNNEL_ADDR = 8080;

    String TEST_NGROK_TUNNEL_HTTPS_NAME = "test-https-tunnel-name";
    String TEST_NGROK_TUNNEL_HTTPS_URI = NgrokApiClient.URI_NGROK_API_TUNNELS + "/test-https-tunnel-name";
    String TEST_NGROK_TUNNEL_HTTPS_PUBLIC_URL = "https://12345678-not-existing.ngrok.io";
    String TEST_NGROK_TUNNEL_HTTPS_PROTO = "https";

    NgrokTunnel TEST_NGROK_TUNNEL_HTTP = NgrokTunnel.builder()
            .name(TEST_NGROK_TUNNEL_HTTP_NAME)
            .uri(TEST_NGROK_TUNNEL_HTTP_URI)
            .publicUrl(TEST_NGROK_TUNNEL_HTTP_PUBLIC_URL)
            .proto(TEST_NGROK_TUNNEL_HTTP_PROTO)
            .metrics(TEST_NGROK_TUNNEL_METRICS)
            .config(NgrokTunnel.NgrokTunnelConfig.builder()
                    .addr(TEST_NGROK_TUNNEL_CONFIG_ADDR)
                    .inspect(TEST_NGROK_TUNNEL_CONFIG_INSPECT)
                    .build()
            ).build();

    NgrokTunnel TEST_NGROK_TUNNEL_HTTPS = NgrokTunnel.builder()
            .name(TEST_NGROK_TUNNEL_HTTPS_NAME)
            .uri(TEST_NGROK_TUNNEL_HTTPS_URI)
            .publicUrl(TEST_NGROK_TUNNEL_HTTPS_PUBLIC_URL)
            .proto(TEST_NGROK_TUNNEL_HTTPS_PROTO)
            .metrics(TEST_NGROK_TUNNEL_METRICS)
            .config(NgrokTunnel.NgrokTunnelConfig.builder()
                    .addr(TEST_NGROK_TUNNEL_CONFIG_ADDR)
                    .inspect(TEST_NGROK_TUNNEL_CONFIG_INSPECT)
                    .build()
            ).build();
}