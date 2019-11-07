package com.example.pingpongvanillajavaclient;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.CountDownLatch;

public class PingPongVanillaJavaClientApplication {

    private static final Logger log = LoggerFactory.getLogger("CLIENT");

    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        RSocket rsocket = RSocketFactory.connect()
            .acceptor(new ClientAcceptor())
            .transport(TcpClientTransport.create(9999))
            .start()
            .block();

        rsocket.requestResponse(DefaultPayload.create("PING"))
            .map(Payload::getDataUtf8)
            .doOnRequest(__ -> log.info("requester >>> PING"))
            .doOnNext(n -> log.info("requester <<< {}", n))
            .doOnTerminate(() -> log.info("requester FIN"))
            .doOnTerminate(latch::countDown)
            .subscribe();

        latch.await();
        rsocket.dispose();
    }

}
