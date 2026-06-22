package com.example.demo;

import com.example.demo.config.EmissionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(EmissionProperties.class)
public class EchoTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(EchoTrackApplication.class, args);
    }
}
