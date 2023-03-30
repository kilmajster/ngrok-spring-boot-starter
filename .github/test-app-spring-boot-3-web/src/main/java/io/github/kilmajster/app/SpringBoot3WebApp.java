package io.github.kilmajster.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@RestController
@SpringBootApplication(scanBasePackages = "io.github.kilmajster.app")
public class SpringBoot3WebApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot3WebApp.class, args);
    }

    public @GetMapping("/")
    String sayHello() {
        return "<h1>Hello World!</h1>";
    }
}
