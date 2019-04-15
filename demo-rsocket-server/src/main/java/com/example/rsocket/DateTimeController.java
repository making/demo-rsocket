package com.example.rsocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

@Controller
public class DateTimeController {

    @MessageMapping("datetime")
    public Flux<Map<String, ZonedDateTime>> datetime(JsonNode n) {
        return Mono.just(n.has("zoneId") ? ZoneId.of(n.get("zoneId").asText()) : ZoneId.systemDefault())
            .map(ZonedDateTime::now)
            .delayElement(Duration.ofSeconds(1))
            .map(x -> Collections.singletonMap("datetime", x))
            .repeat()
            .log("producer");
    }
}
