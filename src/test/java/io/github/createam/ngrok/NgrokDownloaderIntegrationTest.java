package io.github.createam.ngrok;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.createam.ngrok.exception.NgrokDownloadException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = NgrokDownloader.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:/application-integration-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureWireMock(
        port = 1234,
        files = "src/test/resources/")
public class NgrokDownloaderIntegrationTest {

    @Autowired
    private NgrokDownloader ngrokDownloader;

    @Test(expected = NgrokDownloadException.class)
    public void downloadNgrokTo_unreachableResourceThenHintIsLogged() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/__files/ngrok-test-archive.zip"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        ngrokDownloader.downloadNgrokTo("/not-exists/-path");
    }

    @Test
    public void downloadNgrokTo_givenWorkingHostThenFileIsDownloaded() {

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/not-existing-download-url/ngrok-test-archive.zip"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBodyFile("ngrok-test-archive.zip")));

        ngrokDownloader.downloadNgrokTo("not-exists-path");
    }
}