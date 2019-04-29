package com.example.rsocket;

import com.fasterxml.jackson.databind.JsonNode;
import io.rsocket.transport.ClientTransport;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Collections;

@RestController
public class DateTimeController {

    private final RSocketRequester.Builder rsocketRequesterBuilder;

    private final ClientTransport clientTransport;

    public DateTimeController(RSocketRequester.Builder rsocketRequesterBuilder, ClientTransport clientTransport) {
        this.rsocketRequesterBuilder = rsocketRequesterBuilder;
        this.clientTransport = clientTransport;
    }

    @GetMapping(path = "/", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<JsonNode> datetime(ZoneId zoneId) {
        return this.rsocketRequesterBuilder.connect(this.clientTransport, MimeTypeUtils.APPLICATION_JSON)
            .flatMapMany(requester -> requester.route("datetime")
                .data(Collections.singletonMap("zoneId", zoneId))
                .retrieveFlux(JsonNode.class))
            .retryWhen(Retry.anyOf(IOException.class)
                .fixedBackoff(Duration.ofSeconds(1)))
            .log("controller");
    }
}
