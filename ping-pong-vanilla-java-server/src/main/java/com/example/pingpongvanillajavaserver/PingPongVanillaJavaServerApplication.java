package com.example.pingpongvanillajavaserver;

import io.rsocket.RSocketFactory;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import reactor.core.Disposable;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpServer;

import java.util.concurrent.CountDownLatch;

public class PingPongVanillaJavaServerApplication {

    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ServerTransport<?> transport = WebsocketServerTransport.create(HttpServer.from(TcpServer.create().host("localhost").port(9999).wiretap(false)));
        final Disposable disposable = RSocketFactory.receive()
            .acceptor(new ServerAcceptor())
            .transport(transport)
            .start()
            .subscribe();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            latch.countDown();
            disposable.dispose();
        }));

        latch.await();
    }

}
