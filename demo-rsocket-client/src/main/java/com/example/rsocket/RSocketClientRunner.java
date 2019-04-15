package com.example.rsocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Collections;

@Component
public class RSocketClientRunner implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(RSocketClientRunner.class);

    private final RSocketRequester requester;

    public RSocketClientRunner(RSocketRequester requester) {
        this.requester = requester;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Flux<JsonNode> datetime = this.requester.route("datetime")
            .data(Collections.singletonMap("zoneId", "UTC"))
            .retrieveFlux(JsonNode.class)
            .log("consumer");
        datetime.subscribe();
    }
}
