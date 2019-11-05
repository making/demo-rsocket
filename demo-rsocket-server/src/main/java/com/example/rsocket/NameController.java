package com.example.rsocket;

import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@Controller
public class NameController {

    @GetMapping(path = "name", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ResponseBody
    public Flux<Name> personStreamWebFlux() {
        return NameGenerator.stream();
    }

    @MessageMapping("name")
    public Flux<Name> personStreamRsocket() {
        return NameGenerator.stream();
    }
}
