package com.example.rsocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.function.Supplier;

@Component
public class RSocketClientRunner implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(RSocketClientRunner.class);

    private final Supplier<Mono<RSocketRequester>> rSocketRequesterSupplier;

    public RSocketClientRunner(Supplier<Mono<RSocketRequester>> rSocketRequesterSupplier) {
        this.rSocketRequesterSupplier = rSocketRequesterSupplier;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Flux<JsonNode> datetime = this.rSocketRequesterSupplier.get()
            .flatMapMany(requester -> requester.route("datetime")
                .data(Collections.singletonMap("zoneId", "Asia/Tokyo"))
                .retrieveFlux(JsonNode.class))
            .retryWhen(Retry.anyOf(IOException.class)
                .fixedBackoff(Duration.ofSeconds(1))
                .doOnRetry(ctx -> log.warn("Retrying: {}", ctx)))
            .log("consumer");
        datetime.subscribe();
    }
}
