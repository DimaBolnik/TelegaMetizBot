package ru.dev.bolnik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("ru.dev.bolnik.*")
@EntityScan("ru.dev.bolnik.*")
@ComponentScan("ru.dev.bolnik.*")
@SpringBootApplication
public class NodeApp {
    public static void main(String[] args) {
        SpringApplication.run(NodeApp.class);
    }
}
