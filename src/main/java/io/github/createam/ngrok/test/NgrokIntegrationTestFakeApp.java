package io.github.createam.ngrok.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@Profile("ngrok-starter-integration-test")
@SpringBootApplication
public class NgrokIntegrationTestFakeApp {
    public static void main(String[] args) {
        SpringApplication.run(NgrokIntegrationTestFakeApp.class, args);
    }
}
