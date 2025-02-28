package ru.dev.bolnik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("ru.dev.bolnik.*")
@SpringBootApplication
public class MailApp {
    public static void main(String[] args) {
        SpringApplication.run(MailApp.class);
    }
}
