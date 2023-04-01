package io.github.kilmajster.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication(scanBasePackages = "io.github.kilmajster.app")
public class SpringBoot2WebApp {
    public static void main(String... args) {
        SpringApplication.run(SpringBoot2WebApp.class, args);
    }

    public @GetMapping("/")
    String sayHello() {
        return "<h1>Hello World!</h1>";
    }
}
