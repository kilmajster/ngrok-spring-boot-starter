package ngrok.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import ngrok.api.model.NgrokTunnel;
import ngrok.api.rquest.NgrokStartTunnel;
import ngrok.configuration.NgrokConfiguration;
import ngrok.exception.NgrokApiException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static ngrok.TestConstants.*;
import static ngrok.api.NgrokAgentApiClient.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles(TEST_NGROK_PROFILE)
@SpringBootTest(
        classes = {NgrokConfiguration.class, NgrokAgentApiClient.class},
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = TEST_NGROK_PROP_ENABLED)
public class NgrokAgentApiClientIntegrationTest {

    private static final int WIREMOCK_PORT = 4040;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private NgrokAgentApiClient ngrokAgentApiClient;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));
        wireMockServer.start();
        configureFor("localhost", WIREMOCK_PORT);
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void configureWireMock() {
        wireMockServer.resetAll();
        configureFor("localhost", WIREMOCK_PORT);
    }

    @Test
    public void isResponding_shouldReturnTrueWhenNgrokIsRunning() {
        // given
        stubFor(
                get(urlPathEqualTo(URI_NGROK_HTML_STATUS))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())));

        // when
        boolean responding = ngrokAgentApiClient.isResponding();

        // then
        assertThat(responding).isTrue();
    }

    @Test
    public void isResponding_shouldReturnFalseWhenNgrokIsNotWorking() {
        // given
        stubFor(
                get(urlPathEqualTo(URI_NGROK_HTML_STATUS))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        // when
        boolean responding = ngrokAgentApiClient.isResponding();

        // then
        assertThat(responding).isFalse();
    }

    @Test
    public void listTunnels_shouldReturnTunnelsWhenNgrokIsRunning() throws IOException {
        // given
        String tunnelsAsJson = resourceAsString(TEST_NGROK_TUNNELS_LIST_FILE_PATH);

        stubFor(
                get(urlPathEqualTo(URI_NGROK_API_TUNNELS))
                        .willReturn(
                                okJson(tunnelsAsJson)));

        // when
        List<NgrokTunnel> tunnels = ngrokAgentApiClient.listTunnels();

        // then
        assertThat(tunnels)
                .hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(Arrays.asList(TEST_NGROK_TUNNEL_HTTP, TEST_NGROK_TUNNEL_HTTPS));
    }

    private String resourceAsString(final String path) throws IOException {
        File file = ResourceUtils.getFile(this.getClass().getResource(path));
        return FileUtils.readFileToString(file, Charset.defaultCharset());
    }

    @Test
    public void listTunnels_shouldReturnEmptyCollectionWhenNgrokApiRespondWithError() {
        // given
        stubFor(
                get(urlPathEqualTo(URI_NGROK_API_TUNNELS))
                        .willReturn(
                                serverError()));

        // when
        List<NgrokTunnel> tunnels = ngrokAgentApiClient.listTunnels();

        // then
        assertThat(tunnels).isEmpty();
    }

    @Test
    public void startTunnel_shouldStartNgrokTunnel() throws IOException {
        // given
        NgrokStartTunnel ngrokStartTunnelRequest = NgrokStartTunnel.of(TEST_NGROK_TUNNEL_ADDR, TEST_NGROK_TUNNEL_HTTP_PROTO, TEST_NGROK_TUNNEL_HTTP_NAME);
        String tunnelAsJson = resourceAsString(TEST_NGROK_SINGLE_TUNNEL_FILE_PATH);

        stubFor(
                post(urlPathEqualTo(URI_NGROK_API_TUNNELS))
                        .withRequestBody(equalToJson(objectMapper.writeValueAsString(ngrokStartTunnelRequest)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.CREATED.value())
                                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(tunnelAsJson)));

        // when
        final NgrokTunnel ngrokTunnel = ngrokAgentApiClient.startTunnel(TEST_NGROK_TUNNEL_ADDR, TEST_NGROK_TUNNEL_HTTP_PROTO, TEST_NGROK_TUNNEL_HTTP_NAME);

        // then
        assertThat(ngrokTunnel)
                .usingRecursiveComparison()
                .isEqualTo(TEST_NGROK_TUNNEL_HTTP);
    }

    @Test
    public void startTunnel_shouldThrowExceptionWhenNgrokStartFailed() throws IOException {
        // given
        NgrokStartTunnel ngrokStartTunnelRequest = NgrokStartTunnel.of(TEST_NGROK_TUNNEL_ADDR, TEST_NGROK_TUNNEL_HTTP_PROTO, TEST_NGROK_TUNNEL_HTTP_NAME);

        stubFor(
                post(urlPathEqualTo(URI_NGROK_API_TUNNELS))
                        .withRequestBody(equalToJson(objectMapper.writeValueAsString(ngrokStartTunnelRequest)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        // when & then
        assertThatThrownBy(() -> ngrokAgentApiClient.startTunnel(TEST_NGROK_TUNNEL_ADDR, TEST_NGROK_TUNNEL_HTTP_PROTO, TEST_NGROK_TUNNEL_HTTP_NAME))
                .isInstanceOf(NgrokApiException.class)
                .hasMessage("Failed to start ngrok tunnel!");
    }

    @Test
    public void tunnelDetail_shouldReturnTunnelDetailsWhenTunnelExistsForGivenName() throws IOException {
        // given
        String tunnelAsJson = resourceAsString(TEST_NGROK_SINGLE_TUNNEL_FILE_PATH);

        stubFor(
                get(urlPathEqualTo(URI_NGROK_API_TUNNEL_DETAIL.replace("{tunnelName}", TEST_NGROK_TUNNEL_HTTP_NAME)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(tunnelAsJson)));

        // when
        final NgrokTunnel ngrokTunnel = ngrokAgentApiClient.tunnelDetail(TEST_NGROK_TUNNEL_HTTP_NAME);

        // then
        assertThat(ngrokTunnel)
                .usingRecursiveComparison()
                .isEqualTo(TEST_NGROK_TUNNEL_HTTP);
    }

    @Test
    public void tunnelDetail_shouldThrowExceptionWhenTunnelNotExistsForGivenName() {
        // given
        stubFor(
                get(urlPathEqualTo(URI_NGROK_API_TUNNEL_DETAIL.replace("{tunnelName}", TEST_INVALID_TUNNEL_NAME)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.NOT_FOUND.value())
                                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

        // when & then
        assertThatThrownBy(() -> ngrokAgentApiClient.tunnelDetail(TEST_INVALID_TUNNEL_NAME))
                .isInstanceOf(NgrokApiException.class)
                .hasMessage("Failed to fetch details of ngrok tunnel with tunnelName = " + TEST_INVALID_TUNNEL_NAME);
    }

    @Test
    public void stopTunnel_shouldReturnTrueWhenTunnelForGivenNameWasStopped() throws UnsupportedEncodingException {
        // given
        stubFor(
                delete(urlPathEqualTo(URI_NGROK_API_TUNNEL_DETAIL.replace("{tunnelName}", TEST_NGROK_TUNNEL_HTTP_NAME)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.NO_CONTENT.value())));

        // when
        final boolean tunnelStopped = ngrokAgentApiClient.stopTunnel(TEST_NGROK_TUNNEL_HTTP_NAME);

        // then
        assertThat(tunnelStopped).isTrue();
    }

    @Test
    public void stopTunnel_shouldReturnFalseWhenTunnelForGivenNameWasNotStopped() {
        // given
        stubFor(
                delete(urlPathEqualTo(URI_NGROK_API_TUNNEL_DETAIL.replace("{tunnelName}", TEST_INVALID_TUNNEL_NAME)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.NOT_FOUND.value())));

        // when
        final boolean tunnelStopped = ngrokAgentApiClient.stopTunnel(TEST_INVALID_TUNNEL_NAME);

        // then
        assertThat(tunnelStopped).isFalse();
    }
}
