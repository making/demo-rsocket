package com.example.vanillarsocketclient;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class VanillaRsocketClientApplication {

    private static final Logger log = LoggerFactory.getLogger(VanillaRsocketClientApplication.class);

    public static void main(String[] args) throws Exception {
        int port = Optional.ofNullable(System.getenv("PORT")).map(Integer::parseInt)
            .orElse(7000);
        CountDownLatch latch = new CountDownLatch(1);
        ClientTransport transport = WebsocketClientTransport.create(HttpClient.from(TcpClient.create()
            .host("localhost")
            .port(port).wiretap(true)), "/rsocket");
        RSocket rsocket = RSocketFactory.connect()
            .dataMimeType("application/json")
            .metadataMimeType("text/plain")
            .transport(transport)
            .start()
            .block();

        Mono<String> hello = rsocket.requestResponse(DefaultPayload.create("{\"name\":\"Jane Doe\"}", "hello"))
            .map(Payload::getDataUtf8)
            .log("hello");

        Flux<String> datetime = rsocket.requestStream(DefaultPayload.create("{\"zoneId\":\"Asia/Tokyo\"}", "datetime")) //
            .map(Payload::getDataUtf8) //
            .log("datetime");

        hello.and(datetime).subscribe();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            latch.countDown();
            log.info("Shutdown");
        }));
        latch.await();
        rsocket.dispose();
        log.info("Bye");
    }

}
