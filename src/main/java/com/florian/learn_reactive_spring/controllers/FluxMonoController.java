package com.florian.learn_reactive_spring.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxMonoController {
    private final ConnectableFlux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).log().publish();


    @GetMapping("/flux")
    public Flux<Integer> integerFlux() {
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> integerFluxStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/hot/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> integerHotFluxStream() {
        longFlux.connect();
        return longFlux;
    }

    @GetMapping("/mono")
    public Mono<Integer> integerMono() {
        return Mono.just(1)
                .log();
    }
}
