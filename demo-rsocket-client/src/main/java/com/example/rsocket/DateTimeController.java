package com.example.rsocket;

import com.fasterxml.jackson.databind.JsonNode;
import io.rsocket.transport.ClientTransport;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class DateTimeController {

    private final RSocketRequester.Builder rsocketRequesterBuilder;

    private final ClientTransport clientTransport;

    public DateTimeController(RSocketRequester.Builder rsocketRequesterBuilder, ClientTransport clientTransport) {
        this.rsocketRequesterBuilder = rsocketRequesterBuilder;
        this.clientTransport = clientTransport;
    }

    @GetMapping(path = "/", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<JsonNode> datetime() {
        return this.rsocketRequesterBuilder
            .connect(this.clientTransport)
            .flatMapMany(requester -> requester.route("datetime")
                .retrieveFlux(JsonNode.class))
            .retryBackoff(Long.MAX_VALUE, Duration.ofSeconds(10), Duration.ofMinutes(10))
            .log("controller");
    }
}
