package io.github.kilmajster;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(OutputCaptureExtension.class)
class AppTests {

    private final static Logger log = LoggerFactory.getLogger(AppTests.class);
    private static final int WAIT_FOR_STARTUP_SECONDS = 90;
    private static final String HTTPS_NGROK_TUNNEL_REGEX = "(https:\\/\\/)?(([^.]+)\\.)?ngrok\\.io";

    @Test
    public void shouldStartNgrok(CapturedOutput output) throws IOException, URISyntaxException, InterruptedException {
        log.info("[ TEST ] Waiting for ngrok...");

        boolean ngrokStarted = false;

        Thread.sleep(1000);
        for (int i = WAIT_FOR_STARTUP_SECONDS; i > 0; i--) {
            Thread.sleep(1000);
            if (output.toString().contains("Ngrok started successfully!")) {
                ngrokStarted = true;
                log.info("[ TEST ] Ngrok start detected!");
                break;
            }
        }

        assertThat(ngrokStarted).isTrue();

        Pattern pattern = Pattern.compile(HTTPS_NGROK_TUNNEL_REGEX);
        Matcher matcher = pattern.matcher(output.getOut());

        assertThat(matcher.find()).isTrue();
        final String ngrokHttpsRemoteUrl = StringUtils.split(matcher.group(), "[")[1];
        log.info("[ TEST ] Captured ngrok tunnel url = [ {} ]", ngrokHttpsRemoteUrl);

        ResponseEntity<String> responseFromTunnel = new RestTemplate().getForEntity(new URL(ngrokHttpsRemoteUrl).toURI(), String.class);

        log.info("Response from [ {} ] = \n\n\n{}\n\n", ngrokHttpsRemoteUrl, responseFromTunnel.toString());

        assertThat(responseFromTunnel.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseFromTunnel.getBody()).isEqualTo("<h1>Hello World!</h1>");
    }
}