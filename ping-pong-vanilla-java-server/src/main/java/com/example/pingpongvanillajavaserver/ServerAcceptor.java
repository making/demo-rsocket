package com.example.pingpongvanillajavaserver;

import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class ServerAcceptor implements SocketAcceptor {

    private final Logger log = LoggerFactory.getLogger("SERVER");

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {

        return Mono.just(new AbstractRSocket() {

            @Override
            public Mono<Payload> requestResponse(Payload payload) {
                log.info("responder <<< {}", payload.getDataUtf8());

                sendingSocket
                    .requestResponse(DefaultPayload.create("PING"))
                    .doOnRequest(__ -> log.info("\t\trequester >>> PING"))
                    .doOnNext(n -> log.info("\t\trequester <<< {}", n.getDataUtf8()))
                    .doOnTerminate(() -> log.info("\t\trequester FIN"))
                    .subscribe();

                return Mono.just(DefaultPayload.create("PONG"))
                    .doOnNext(n -> log.info("responder >>> {}", n.getDataUtf8()))
                    .doOnTerminate(() -> log.info("responder FIN"));
            }
        });
    }
}
