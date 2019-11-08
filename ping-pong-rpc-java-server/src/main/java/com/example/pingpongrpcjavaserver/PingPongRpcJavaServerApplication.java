package com.example.pingpongrpcjavaserver;

import com.example.pingpong.PingPongService;
import com.example.pingpong.PingPongServiceServer;
import io.rsocket.RSocketFactory;
import io.rsocket.rpc.rsocket.RequestHandlingRSocket;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class PingPongRpcJavaServerApplication {

    public static void main(String[] args) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final PingPongService pingPongService = new PingPongServiceImpl();
        final PingPongServiceServer server = new PingPongServiceServer(pingPongService, Optional.empty(), Optional.empty());
        final Disposable disposable = RSocketFactory.receive()
            .acceptor((setup, sendingSocket) -> Mono.just(new RequestHandlingRSocket(server)))
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
