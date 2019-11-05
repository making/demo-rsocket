package com.example.rsocket;

import com.fasterxml.jackson.databind.JsonNode;
import io.rsocket.transport.ClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class NameController {

    private final Logger log = LoggerFactory.getLogger(NameController.class);

    private final Mono<RSocketRequester> requesterMono;

    public NameController(RSocketRequester.Builder builder, ClientTransport clientTransport) {
        this.requesterMono = builder.connect(clientTransport);
    }

    @GetMapping(path = "/names", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<JsonNode> names() {
        return this.requesterMono
            .flatMapMany(requester -> requester.route("name")
                .retrieveFlux(JsonNode.class))
            .delayElements(Duration.ofMillis(100))
            .retryBackoff(Long.MAX_VALUE, Duration.ofSeconds(10), Duration.ofMinutes(10))
            .log("name");
    }
}
