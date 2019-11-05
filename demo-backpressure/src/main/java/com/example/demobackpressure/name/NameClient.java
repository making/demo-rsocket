package com.example.demobackpressure.name;

import reactor.core.publisher.Flux;

public interface NameClient {

    Flux<Name> names();
}
