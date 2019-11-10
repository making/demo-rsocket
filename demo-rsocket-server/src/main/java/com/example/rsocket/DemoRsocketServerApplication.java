package com.example.rsocket;

import io.micrometer.core.instrument.MeterRegistry;
import io.rsocket.micrometer.MicrometerRSocketInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.rsocket.server.ServerRSocketFactoryProcessor;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoRsocketServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoRsocketServerApplication.class, args);
    }

    @Bean
    public ServerRSocketFactoryProcessor serverRSocketFactoryProcessor(MeterRegistry meterRegistry) {
        return factory -> factory.addResponderPlugin(new MicrometerRSocketInterceptor(meterRegistry));
    }
}
