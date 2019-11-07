package com.example.vanillarsocketserver;

import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpServer;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

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

}
