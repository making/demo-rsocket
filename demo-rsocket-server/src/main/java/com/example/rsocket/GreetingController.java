package com.example.rsocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

@Controller
public class GreetingController {

    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    @MessageMapping("greeting/{name}")
    public Mono<Greeting> hello(@DestinationVariable("name") String name) {
        return Mono.just(new Greeting(this.counter.incrementAndGet(), String.format(template, name)));
    }
}
