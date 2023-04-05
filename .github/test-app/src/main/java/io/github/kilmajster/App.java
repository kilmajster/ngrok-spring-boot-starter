package io.github.kilmajster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class App {
	public static void main(String... args) { SpringApplication.run(App.class, args); }

	public @GetMapping("/") String sayHello() { return "<h1>Hello World!</h1>"; }
}
