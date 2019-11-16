package io.github.kilmajster.ngrok.control;

import io.github.kilmajster.ngrok.configuration.NgrokAutoConfiguration;
import io.github.kilmajster.ngrok.exception.NgrokDownloadException;
import io.github.kilmajster.ngrok.test.NgrokIntegrationTestFakeApp;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {NgrokIntegrationTestFakeApp.class, NgrokDownloader.class, NgrokAutoConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:/application-integration-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureWireMock(files = "src/test/resources", port = 8083)
public class NgrokDownloaderIntegrationTest {

    @Autowired
    private NgrokDownloader ngrokDownloader;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(ngrokDownloader, "windowsBinaryUrl", "http://localhost:8083");
        ReflectionTestUtils.setField(ngrokDownloader, "osxBinaryUrl", "http://localhost:8083");
        ReflectionTestUtils.setField(ngrokDownloader, "linuxBinaryUrl", "http://localhost:8083");
    }

    @AfterClass
    public static void cleanup() {
        File file = new File("/not-exists-path");

        if (file.exists()) {
            file.delete();
        }
    }

    @Test(expected = NgrokDownloadException.class)
    public void downloadNgrokTo_unreachableResourceThenHintIsLogged() {
        stubFor(
        get(urlPathMatching("/__files/ngrok-test-archive.zip"))
        .willReturn(
        aResponse()
        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        ngrokDownloader.downloadNgrokTo("/not-exists-path");
    }

    @Ignore
    @Test
    public void downloadNgrokTo_givenWorkingHostThenFileIsDownloaded() {
        stubFor(
        get(urlPathMatching("/not-existing-download-url/ngrok-test-archive.zip"))
        .willReturn(
        aResponse()
        .withStatus(HttpStatus.OK.value())
        .withBodyFile("ngrok-test-archive.zip")));

        ngrokDownloader.downloadNgrokTo("not-exists-path");
    }
}