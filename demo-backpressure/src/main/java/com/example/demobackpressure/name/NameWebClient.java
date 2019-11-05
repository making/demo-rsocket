package com.example.demobackpressure.name;

import com.example.demobackpressure.DemoConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@Profile("default")
public class NameWebClient implements NameClient {

    private final WebClient webClient;

    public NameWebClient(WebClient.Builder builder, DemoConfig config) {
        this.webClient = builder
            .baseUrl(config.webUrl())
            .build();
    }


    @Override
    public Flux<Name> names() {
        return this.webClient.get().uri("/name")
            .retrieve()
            .bodyToFlux(Name.class);
    }
}
