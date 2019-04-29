package com.example.rsocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Controller
public class HelloController {

    @MessageMapping("hello")
    public Mono<Map<String, String>> hello(Map<String, String> body) {
        String name = Objects.toString(body.get("name"), "No Name");
        return Mono.just(Collections.singletonMap("message", "Hello " + name + "!"));
    }
}
