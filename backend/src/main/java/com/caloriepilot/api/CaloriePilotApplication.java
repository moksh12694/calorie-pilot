package com.caloriepilot.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CaloriePilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaloriePilotApplication.class, args);
    }
}
