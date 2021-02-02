package com.florian.learn_reactive_spring.flux_mono_playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FLuxMonoFactoryTest {
    List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

    @Test
    void fluxUsingIterable() {
        Flux<Integer> numberIntegerFlux = Flux.fromIterable(numbers).log();
        StepVerifier.create(numberIntegerFlux)
                .expectNext(1, 2, 3, 4, 5, 6)
                .verifyComplete();
    }

    @Test
    void fluxFromArray() {
        Integer[] arr = {1, 2, 3};
        Flux<Integer> integerFlux = Flux.fromArray(arr);
        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void fluxFromStream() {
        Flux<Integer> integerFlux = Flux.fromStream(numbers.stream()).log();
        System.out.println(StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3, 4, 5, 6)
                .verifyComplete().toMillis());
    }

    @Test
    void monoJustOrEmpty() {
        Mono<Integer> integerMono = Mono.justOrEmpty(null);
        StepVerifier.create(integerMono.log())
                .verifyComplete();
    }

    @Test
    void monoUsingSupplier() {
        Supplier<Integer> supplier = () -> 2;
        Mono<Integer> integerMono = Mono.fromSupplier(supplier);
        StepVerifier.create(integerMono.log())
                .expectNext(2)
                .verifyComplete();
    }

    @Test
    void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(0, 4);
        StepVerifier.create(integerFlux.log())
                .expectNext(0, 1, 2, 3)
                .verifyComplete();
    }

}
