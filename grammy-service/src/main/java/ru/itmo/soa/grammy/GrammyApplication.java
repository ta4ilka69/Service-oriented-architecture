package ru.itmo.soa.grammy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ru.itmo.soa.grammy")
public class GrammyApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(GrammyApplication.class, args);
    }
}


