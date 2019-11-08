package com.example.pingpongrpcjavaclient;

import com.example.pingpong.PingPongService;
import com.example.pingpong.PingPongServiceClient;
import com.example.pingpong.PingRequest;
import com.example.pingpong.PongResponse;
import io.netty.buffer.ByteBufAllocator;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;

public class PingPongRpcJavaClientApplication {

    public static void main(String[] args) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        RSocket rsocket = RSocketFactory.connect()
            .transport(WebsocketClientTransport.create(9999))
            .start()
            .block();

        final PingPongService pingPongService = new PingPongServiceClient(rsocket);

        final Mono<PongResponse> response = pingPongService.ping(PingRequest.newBuilder().setMessage("PING").build(), ByteBufAllocator.DEFAULT.buffer());
        response.map(PongResponse::getMessage)
            .log("client")
            .doOnTerminate(latch::countDown)
            .subscribe();

        latch.await();
        rsocket.dispose();
    }

}
