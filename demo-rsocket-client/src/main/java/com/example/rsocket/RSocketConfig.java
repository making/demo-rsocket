package com.example.rsocket;

import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

@ConfigurationProperties(prefix = "producer")
public class RSocketConfig {

    private final String host;

    private final int port;

    public RSocketConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public ClientTransport clientTransport() {
        return TcpClientTransport.create(this.host, this.port);
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
