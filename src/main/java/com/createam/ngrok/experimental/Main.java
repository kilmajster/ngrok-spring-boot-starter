package com.createam.ngrok.experimental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.createam.ngrok")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }
}
