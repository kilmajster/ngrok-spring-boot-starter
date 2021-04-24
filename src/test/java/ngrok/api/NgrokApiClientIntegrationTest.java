package ngrok.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import ngrok.TestConstants;
import ngrok.api.model.NgrokTunnel;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(TestConstants.TEST_NGROK_PROFILE)
@AutoConfigureWireMock(port = 4040)
@SpringBootTest(
        classes = NgrokApiClient.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = TestConstants.TEST_NGROK_PROP_ENABLED)
public class NgrokApiClientIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private NgrokApiClient ngrokApiClient;



    @Test
    public void isResponding_shouldReturnTrueWhenNgrokIsRunning() {
        // given
        stubFor(
        get(urlPathMatching("/status"))
        .willReturn(aResponse()
        .withStatus(HttpStatus.OK.value())));

        // when
        boolean responding = ngrokApiClient.isResponding();

        // then
        assertThat(responding).isTrue();
    }

    @Test
    public void isResponding_shouldReturnFalseWhenNgrokIsNotWorking() {
        // given
        stubFor(
        get(urlPathMatching("/status"))
        .willReturn(aResponse()
        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        // when
        boolean responding = ngrokApiClient.isResponding();

        // then
        assertThat(responding).isFalse();
    }

    @Test
    public void listTunnels_shouldReturnTunnelsWhenNgrokIsRunning() throws IOException {
        // given
        String tunnelsAsJson = resourceAsString(TestConstants.TEST_NGROK_TUNNELS_LIST_FILE_PATH);

        stubFor(
        get(urlPathMatching("/api/tunnels"))
        .willReturn(
        okJson(tunnelsAsJson)));

        // when
        List<NgrokTunnel> tunnels = ngrokApiClient.listTunnels();

        // then
        assertThat(tunnels).hasSize(2);
        assertThat(tunnels).extracting(NgrokTunnel::getProto).contains("http", "https");
        assertThat(tunnels).extracting(NgrokTunnel::getPublicUrl).contains("https://12345678-not-existing.ngrok.io", "http://12345678-not-existing.ngrok.io");
    }

    private String resourceAsString(final String path) throws IOException {
        File file = ResourceUtils.getFile(this.getClass().getResource(path));
        return FileUtils.readFileToString(file, Charset.defaultCharset());
    }

    @Test
    public void listTunnels_shouldReturnEmptyCollectionWhenNgrokApiRespondWithError() {
        // given
        stubFor(
        get(urlPathMatching("/api/tunnels"))
        .willReturn(
        serverError()));

        // when
        List<NgrokTunnel> tunnels = ngrokApiClient.listTunnels();

        // then
        assertThat(tunnels).isEmpty();
    }

    ///////// TODO
    @Test
    public void startTunnel_shouldStartNgrokTunnel() throws IOException {
        NgrokApiClient.NgrokStartTunnel ngrokStartTunnelRequest = NgrokApiClient.NgrokStartTunnel.of("8080", "http", "test_tunnel_name");
        String tunnelAsJson = resourceAsString(TestConstants.TEST_NGROK_SINGLE_TUNNEL_FILE_PATH);

        // given
        stubFor(
        post(urlPathEqualTo("/api/tunnels"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(ngrokStartTunnelRequest)))
        .willReturn(
        aResponse()
        .withBody(tunnelAsJson)));

        // when
        final NgrokTunnel ngrokTunnel = ngrokApiClient.startTunnel("8080", "http", "test_tunnel_name");

        // then

    }

    @Test
    public void startTunnel_shouldThrowExceptionWhenNgrokStartFailed() {

    }
}