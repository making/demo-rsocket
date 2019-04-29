package com.example.rsocket;

import com.fasterxml.jackson.databind.JsonNode;
import io.rsocket.transport.ClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

@Component
public class RSocketClientRunner implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(RSocketClientRunner.class);

    private final RSocketRequester.Builder rsocketRequesterBuilder;

    private final ClientTransport clientTransport;

    public RSocketClientRunner(RSocketRequester.Builder rsocketRequesterBuilder, ClientTransport clientTransport) {
        this.rsocketRequesterBuilder = rsocketRequesterBuilder;
        this.clientTransport = clientTransport;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Mono<JsonNode> hello = this.rsocketRequesterBuilder
            .connect(this.clientTransport, MimeTypeUtils.APPLICATION_JSON)
            .flatMap(requester -> requester.route("hello")
                .data(Collections.singletonMap("name", "Jane Doe"))
                .retrieveMono(JsonNode.class))
            .log("hello");
        Flux<JsonNode> datetime = this.rsocketRequesterBuilder
            .connect(this.clientTransport, MimeTypeUtils.APPLICATION_JSON)
            .flatMapMany(requester -> requester.route("datetime")
                .data(Collections.singletonMap("zoneId", "Asia/Tokyo"))
                .retrieveFlux(JsonNode.class))
            .retryWhen(Retry.anyOf(IOException.class)
                .fixedBackoff(Duration.ofSeconds(1))
                .doOnRetry(ctx -> log.warn("Retrying: {}", ctx)))
            .log("datetime");
        hello.and(datetime).subscribe();
    }
}
