package io.github.kilmajster.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication(scanBasePackages = "io.github.kilmajster.app")
public class SpringBoot2ReactiveApp {
    public static void main(String... args) {
        SpringApplication.run(SpringBoot2ReactiveApp.class, args);
    }

    public @GetMapping("/")
    Mono<String> sayHello() {
        return Mono.just("<h1>Hello World!</h1>");
    }
}
