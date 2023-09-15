package io.github.kilmajster;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(OutputCaptureExtension.class)
class AppTests {

	static final int WAIT_FOR_STARTUP_SECONDS = 90;

	@LocalServerPort
	int serverPort;


	@Test
	void should_start_ngrok_and_log_tunnel_details(CapturedOutput output) throws IOException, URISyntaxException, InterruptedException {
		System.out.println("[automation-test] Waiting for ngrok startup confirmation in output logs...");
		waitForNgrokStartConfirmationInLogs(output);
		final String ngrokHttpsTunnelUrl = extractNgrokHttpsTunnelUrlFromLogs(output);
		System.out.println("[automation-test] Ngrok tunnel is running between ::" + serverPort + " <-> " + ngrokHttpsTunnelUrl);

		System.out.println("[automation-test] Executing GET request...");
		long timerStart = System.currentTimeMillis();
		final ResponseEntity<String> responseFromTunnel = new RestTemplate()
				.getForEntity(
						new URL(ngrokHttpsTunnelUrl).toURI(),
						String.class
				);
		long timerStop = System.currentTimeMillis();
		System.out.println("[automation-test] " + ngrokHttpsTunnelUrl + " responded in "
				+ (timerStop - timerStart) + "ms with\n\n" + responseFromTunnel + "\n");

		assertThat(responseFromTunnel.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseFromTunnel.getBody()).isEqualTo("<h1>Hello World!</h1>");
	}

	private String extractNgrokHttpsTunnelUrlFromLogs(CapturedOutput output) {
		return Arrays.stream(StringUtils.split(output.toString(), " "))
				.filter(this::isNgrokAppLink).findFirst().get();
	}

	private boolean isNgrokAppLink(String s) {
		return s != null
				&& s.startsWith("https://")
				&& StringUtils.containsAny(s, "ngrok.io", "ngrok-free.app", "ngrok.app");
	}

	private void waitForNgrokStartConfirmationInLogs(CapturedOutput output) throws InterruptedException {
		for (int i = WAIT_FOR_STARTUP_SECONDS; i > 0; i--) {
			Thread.sleep(1000);
			if (output.toString().contains("Ngrok started successfully!") || output.toString().contains("Ngrok was already running!")) {
				Thread.sleep(2000);
				return;
			}
		}
		fail("Ngrok not started!");
	}
}
