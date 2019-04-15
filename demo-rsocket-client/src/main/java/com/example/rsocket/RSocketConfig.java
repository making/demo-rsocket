package com.example.rsocket;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;

@ConfigurationProperties(prefix = "producer")
public class RSocketConfig {

    private final String host;

    private final int port;

    public RSocketConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public RSocketRequester requester(RSocketStrategies strategies) {
        return RSocketRequester.create(rSocket(), MimeTypeUtils.APPLICATION_JSON, strategies);
    }

    @Bean
    public RSocket rSocket() {
        return RSocketFactory
            .connect()
            .dataMimeType(MediaType.APPLICATION_JSON_VALUE)
            .frameDecoder(PayloadDecoder.ZERO_COPY)
            .transport(TcpClientTransport.create(this.host, this.port))
            .start()
            .block();
    }
}
