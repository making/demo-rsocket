package com.example.demobackpressure.name;

import com.example.demobackpressure.DemoConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Profile("rsocket")
public class NameRSocketClient implements NameClient {

    private final RSocketRequester requester;

    public NameRSocketClient(RSocketRequester.Builder builder, DemoConfig config) {
        this.requester = builder.connect(config.clientTransport()).block();
    }

    @Override
    public Flux<Name> names() {
        return this.requester.route("name").retrieveFlux(Name.class);
    }
}
