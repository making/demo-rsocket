package com.example.rsocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.ZoneId;
import java.util.Collections;

@RestController
public class DateTimeController {

    private final RSocketRequester requester;

    public DateTimeController(RSocketRequester requester) {
        this.requester = requester;
    }

    @GetMapping(path = "/", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<JsonNode> datetime(ZoneId zoneId) {
        return this.requester.route("datetime")
            .data(Collections.singletonMap("zoneId", zoneId))
            .retrieveFlux(JsonNode.class);
    }
}
