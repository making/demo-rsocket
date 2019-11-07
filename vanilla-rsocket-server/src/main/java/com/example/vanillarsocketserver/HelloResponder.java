package com.example.vanillarsocketserver;

import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

class HelloResponder implements SocketAcceptor {

    private static final Logger log = LoggerFactory.getLogger(HelloResponder.class);

    static final String template = "{\"id\":%d,\"content\":\"Hello World!\"}";

    final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
        log.info("metadataMimeType={}", setup.metadataMimeType());
        log.info("dataMimeType={}", setup.dataMimeType());
        return Mono.just(new AbstractRSocket() {

            @Override
            public Mono<Payload> requestResponse(Payload payload) {
                return Mono.just(DefaultPayload.create(String.format(template, counter.incrementAndGet()))).log("hello");
            }

            @Override
            public Flux<Payload> requestStream(Payload payload) {
                return NameGenerator.stream().map(DefaultPayload::create);
            }
        });
    }
}
