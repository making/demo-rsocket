package com.example.rsocket;

import com.fasterxml.jackson.databind.JsonNode;
import io.rsocket.transport.ClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;

@Component
public class RSocketClientRunner implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(RSocketClientRunner.class);

    private final RSocketRequester requester;

    public RSocketClientRunner(RSocketRequester.Builder builder, ClientTransport clientTransport) {
        this.requester = builder.connect(clientTransport).block(Duration.ofSeconds(10));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Mono<JsonNode> hello = this.requester.route("hello")
            .data(Collections.singletonMap("name", "Jane Doe"))
            .retrieveMono(JsonNode.class)
            .log("hello");
        hello.block();
    }
}
