package com.example.rsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DemoRsocketClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoRsocketClientApplication.class, args);
    }

}
