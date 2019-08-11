package io.github.kilmajster.ngrok.control;

import io.github.kilmajster.ngrok.data.NgrokTunnel;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = NgrokApiClient.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:/application-integration-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureWireMock(port = 4040)
public class NgrokApiClientIntegrationTest {

    @Autowired
    private NgrokApiClient ngrokApiClient;

    @Test
    public void isResponding_shouldReturnTrueWhenNgrokIsRunning() {
        // given
        stubFor(get(urlPathMatching("/status"))
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
        stubFor(get(urlPathMatching("/status"))
                .willReturn(aResponse()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        // when
        boolean responding = ngrokApiClient.isResponding();

        // then
        assertThat(responding).isFalse();
    }

    @Test
    public void fetchTunnels_shouldReturnTunnelsWhenNgrokIsRunning() throws IOException {
        // given
        File file = ResourceUtils.getFile(this.getClass().getResource("/tunnels.json"));
        String tunnelsAsJson = FileUtils.readFileToString(file, Charset.defaultCharset());

        stubFor(get(urlPathMatching("/api/tunnels"))
                .willReturn(
                        okJson(tunnelsAsJson)));
        // when

        List<NgrokTunnel> tunnels = ngrokApiClient.fetchTunnels();

        // then

        assertThat(tunnels).hasSize(2);
        assertThat(tunnels).extracting(NgrokTunnel::getProto).contains("http", "https");
        assertThat(tunnels).extracting(NgrokTunnel::getPublicUrl).contains("https://12345678-not-existing.ngrok.io", "http://12345678-not-existing.ngrok.io");
    }

    @Test
    public void fetchTunnels_shouldReturnEmptyCollectionWhenNgrokApiRespondWithError() {
        // given

        stubFor(get(urlPathMatching("/api/tunnels")).willReturn(serverError()));
        // when

        List<NgrokTunnel> tunnels = ngrokApiClient.fetchTunnels();

        // then
        assertThat(tunnels).isEmpty();
    }
}