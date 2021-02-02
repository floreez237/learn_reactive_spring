package com.florian.learn_reactive_spring.flux_mono_playground;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {


    @Test
    void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Florian", "The Best")
//                .concatWith(Flux.error(new RuntimeException("Test")))
                .concatWith(Mono.just("test"))
                .log();


        stringFlux
                .subscribe(System.out::println, System.err::println, () -> System.out.println("Completed"));
    }

    @Test
    void fluxTestElementsWithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Florian", "The Best").log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Florian", "The Best")
                .verifyComplete();// it is responsible to make the subscribe call to the publishers
    }

    @Test
    void fluxTestElementsWithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Florian", "The Best")
                .concatWith(Flux.error(new RuntimeException("Test")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Florian", "The Best")
//                .expectError(RuntimeException.class)
                .expectErrorMessage("Test")
                .verify();// it is responsible to make the subscribe call to the publishers

    }

    @Test
    @DisplayName("Testing the number of element in the flux")
    void fluxTestElementsCount_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Florian", "The Best")
                .concatWith(Flux.error(new RuntimeException("Test")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
//                .expectError(RuntimeException.class)
                .expectErrorMessage("Test")
                .verify();// it is responsible to make the subscribe call to the publishers

    }

    @Test
    void monoTest() {
        Mono<String> stringMono = Mono.just("Spring").log();
        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    @DisplayName("Testing Mono with erro")
    void monoTestWithError() {
        StepVerifier.create(Mono.error(new RuntimeException("Test")))
                .expectError(RuntimeException.class)
                .verify();
    }
}
