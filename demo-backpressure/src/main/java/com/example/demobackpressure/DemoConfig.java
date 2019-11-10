package com.example.demobackpressure;

import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@ConfigurationProperties(prefix = "producer")
@ConstructorBinding
public class DemoConfig {

    private final String host;

    private final int port;

    private final boolean wiretap;

    public DemoConfig(String host, int port, boolean wiretap) {
        this.host = host;
        this.port = port;
        this.wiretap = wiretap;
    }


    public String webUrl() {
        return String.format("http://%s:%d", this.host, this.port);
    }

    @Bean
    public ClientTransport clientTransport() {
        return WebsocketClientTransport
            .create(HttpClient.from(TcpClient.create().host(this.host).port(this.port)).wiretap(this.wiretap), "/rsocket");
    }
}
