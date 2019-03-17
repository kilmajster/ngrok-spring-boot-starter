package io.github.createam.ngrok;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void isResponding_shouldReturnTrueWhenNgrokIsUp() {
        // given
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/status"))
                .willReturn(WireMock.aResponse()
                .withStatus(HttpStatus.OK.value())));

        // when
        boolean responding = ngrokApiClient.isResponding();

        // then
        assertThat(responding).isTrue();
    }

    @Test
    public void isResponding_shouldReturnFalseWhenNgrokIsNotWorking() {
        // given
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/status"))
                .willReturn(WireMock.aResponse()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        // when
        boolean responding = ngrokApiClient.isResponding();

        // then
        assertThat(responding).isFalse();
    }
}