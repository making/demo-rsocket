package com.example.rsocket;

import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

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
    public Supplier<Mono<RSocketRequester>> rSocketRequesterSupplier(ClientTransport clientTransport, RSocketStrategies strategies) {
        return () ->
            RSocketFactory
                .connect()
                .dataMimeType(MediaType.APPLICATION_JSON_VALUE)
                .frameDecoder(PayloadDecoder.ZERO_COPY)
                .transport(clientTransport)
                .start()
                .map(rSocket -> RSocketRequester.create(rSocket, MimeTypeUtils.APPLICATION_JSON, strategies));
    }
}
