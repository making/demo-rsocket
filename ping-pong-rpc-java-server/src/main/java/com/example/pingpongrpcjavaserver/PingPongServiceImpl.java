package com.example.pingpongrpcjavaserver;

import com.example.pingpong.PingPongService;
import com.example.pingpong.PingRequest;
import com.example.pingpong.PongResponse;
import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Mono;

public class PingPongServiceImpl implements PingPongService {

    @Override
    public Mono<PongResponse> ping(PingRequest message, ByteBuf metadata) {
        return Mono.just(PongResponse.newBuilder().setMessage("PONG").build());
    }
}
