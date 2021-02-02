package com.florian.learn_reactive_spring.flux_mono_playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoCombineTest {
    @Test
    void combineUsingMergeWithDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
        Flux<String> mergeFlux = Flux.merge(flux1, flux2).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNextCount(6)
//                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");
        Flux<String> mergeFlux = Flux.merge(flux1, flux2).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNextCount(6)
//                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }


    @Test
    void combineUsingConcat() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");
        Flux<String> mergeFlux = Flux.concat(flux1, flux2).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNextCount(6)
//                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void combineUsingConcatWithDelay() {
        VirtualTimeScheduler.getOrSet();
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
        Flux<String> mergeFlux = Flux.concat(flux1, flux2).log();


        StepVerifier.withVirtualTime(() -> mergeFlux)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))//this the key to speed up the process
                .expectNextCount(6)
//                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void combineUsingZip() {
        /*
         * The sip method is used to merge fluxes by carrying out an operation on each element emitted by each flux
         * so as to create a single element to input in the final flux
         * The operation ends when flux emits an onComplete or onError Signal
         * */
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");
        Flux<String> mergeFlux = Flux.zip(flux1, flux2, String::concat).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

}
