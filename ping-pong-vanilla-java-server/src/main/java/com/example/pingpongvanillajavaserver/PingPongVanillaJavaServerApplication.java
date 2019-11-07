package com.example.pingpongvanillajavaserver;

import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.Disposable;

import java.util.concurrent.CountDownLatch;

public class PingPongVanillaJavaServerApplication {

    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Disposable disposable = RSocketFactory.receive()
            .acceptor(new ServerAcceptor())
            .transport(TcpServerTransport.create(9999))
            .start()
            .subscribe();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            latch.countDown();
            disposable.dispose();
        }));

        latch.await();
    }

}
