package com.example.vanillarsocketclient;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class VanillaRsocketClientApplication {

    private static final Logger log = LoggerFactory.getLogger(VanillaRsocketClientApplication.class);

    public static void main(String[] args) throws Exception {
        int port = Optional.ofNullable(System.getenv("PORT")).map(Integer::parseInt)
            .orElse(7000);
        CountDownLatch latch = new CountDownLatch(1);
        ClientTransport transport = WebsocketClientTransport.create(HttpClient.from(TcpClient.create().host("localhost").port(port).wiretap(false)), "/rsocket");
        RSocket rsocket = RSocketFactory.connect()
            .frameDecoder(PayloadDecoder.ZERO_COPY)
            .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_ROUTING.getString())
            .dataMimeType(WellKnownMimeType.APPLICATION_JSON.getString())
            .transport(transport)
            .start()
            .block();

        Mono<String> hello = rsocket.requestResponse(DefaultPayload.create(DefaultPayload.EMPTY_BUFFER, routingMetadata("greeting.Jane")))
            .map(Payload::getDataUtf8)
            .log("hello");

        hello
            .doOnTerminate(latch::countDown)
            .subscribe();
        latch.await();
        rsocket.dispose();
        log.info("Bye");
    }

    /**
     * https://github.com/rsocket/rsocket/blob/master/Extensions/Routing.md
     */
    static ByteBuffer routingMetadata(String tag) {
        final byte[] bytes = tag.getBytes(StandardCharsets.UTF_8);
        final ByteBuffer buffer = ByteBuffer.allocate(1 + bytes.length);
        buffer.put((byte) bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }
}
