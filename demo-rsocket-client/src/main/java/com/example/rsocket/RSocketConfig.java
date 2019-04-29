package com.example.rsocket;

import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@ConfigurationProperties(prefix = "producer")
public class RSocketConfig {

    private final String host;

    private final int port;

    private final boolean wiretap;

    public RSocketConfig(String host, int port, boolean wiretap) {
        this.host = host;
        this.port = port;
        this.wiretap = wiretap;
    }

    @Bean
    public ClientTransport clientTransport() {
        return WebsocketClientTransport
            .create(HttpClient.from(TcpClient.create().host(this.host).port(this.port)).wiretap(this.wiretap), "/rsocket");
    }

    @Bean
    public RSocketRequester.Builder rsocketRequesterBuilder(RSocketStrategies strategies) {
        return RSocketRequester.builder()
            .rsocketFactory(factory -> factory.frameDecoder(PayloadDecoder.ZERO_COPY))
            .rsocketStrategies(builder -> builder
                .dataBufferFactory(strategies.dataBufferFactory())
                .decoders(decoders -> decoders.addAll(strategies.decoders()))
                .encoders(encoders -> encoders.addAll(strategies.encoders()))
                .reactiveAdapterStrategy(strategies.reactiveAdapterRegistry()));
    }
}
