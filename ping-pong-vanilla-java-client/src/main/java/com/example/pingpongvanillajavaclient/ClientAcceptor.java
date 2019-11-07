package com.example.pingpongvanillajavaclient;

import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class ClientAcceptor implements SocketAcceptor {

    private final Logger log = LoggerFactory.getLogger("CLIENT");

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
        return Mono.just(new AbstractRSocket() {

            @Override
            public Mono<Payload> requestResponse(Payload payload) {
                log.info("\t\tresponder <<< {}", payload.getDataUtf8());
                return Mono.just(DefaultPayload.create("PONG"))
                    .doOnNext(n -> log.info("\t\tresponder >>> {}", n.getDataUtf8()))
                    .doOnTerminate(() -> log.info("\t\tresponder FIN"));
            }
        });
    }
}
