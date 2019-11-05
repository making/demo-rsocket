package com.example.vanillarsocketserver;

import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpServer;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class VanillaRsocketServerApplication {

    private static final Logger log = LoggerFactory.getLogger(VanillaRsocketServerApplication.class);


    public static void main(String[] args) throws Exception {
        int port = Optional.ofNullable(System.getenv("PORT")).map(Integer::parseInt)
            .orElse(7000);
        CountDownLatch latch = new CountDownLatch(1);
        ServerTransport<?> transport = WebsocketServerTransport.create(HttpServer.from(TcpServer.create().host("localhost").port(port).wiretap(false)));
        final Disposable disposable = RSocketFactory.receive()
            .frameDecoder(PayloadDecoder.ZERO_COPY)
            .acceptor(new HelloResponder())
            .transport(transport)
            .start()
            .subscribe();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            latch.countDown();
            disposable.dispose();
        }));
        latch.await();
        log.info("Bye");
    }

    static class HelloResponder implements SocketAcceptor {

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
}
