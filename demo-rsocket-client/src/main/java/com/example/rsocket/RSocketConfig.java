package com.example.rsocket;

import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@ConfigurationProperties(prefix = "producer")
@ConstructorBinding
@Component
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
}
