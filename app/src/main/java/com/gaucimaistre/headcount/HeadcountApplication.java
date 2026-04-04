package com.gaucimaistre.headcount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HeadcountApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeadcountApplication.class, args);
    }
}
