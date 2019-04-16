package com.example.rsocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Collections;
import java.util.function.Supplier;

@RestController
public class DateTimeController {

    private final Supplier<Mono<RSocketRequester>> rSocketRequesterSupplier;

    public DateTimeController(Supplier<Mono<RSocketRequester>> rSocketRequesterSupplier) {
        this.rSocketRequesterSupplier = rSocketRequesterSupplier;
    }

    @GetMapping(path = "/", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<JsonNode> datetime(ZoneId zoneId) {
        return this.rSocketRequesterSupplier.get()
            .flatMapMany(requester -> requester.route("datetime")
                .data(Collections.singletonMap("zoneId", zoneId))
                .retrieveFlux(JsonNode.class))
            .retryWhen(Retry.anyOf(IOException.class)
                .fixedBackoff(Duration.ofSeconds(1)))
            .log("controller");
    }
}
