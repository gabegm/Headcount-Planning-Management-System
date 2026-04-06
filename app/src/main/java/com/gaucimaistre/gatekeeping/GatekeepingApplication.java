package com.gaucimaistre.gatekeeping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GatekeepingApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatekeepingApplication.class, args);
    }
}
