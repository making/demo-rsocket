package com.example.rsocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

@Controller
public class DateTimeController {

    @MessageMapping("datetime")
    public Flux<Map<String, ZonedDateTime>> datetime() {
        return Mono.fromCallable(ZonedDateTime::now)
            .delayElement(Duration.ofSeconds(1))
            .map(x -> Collections.singletonMap("datetime", x))
            .repeat()
            .log("producer");
    }
}
