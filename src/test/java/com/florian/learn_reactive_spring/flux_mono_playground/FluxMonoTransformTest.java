package com.florian.learn_reactive_spring.flux_mono_playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxMonoTransformTest {
    List<String> names = Arrays.asList("Florian", "Yasmine", "Lowe");

    @Test
    void transformWithMap() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("FLORIAN", "YASMINE", "LOWE")
                .verifyComplete();
    }

    @Test
    void transformWithMap_Length() {
        Flux<Integer> stringFlux = Flux.fromIterable(names)
                .map(String::length)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext(7, 7, 4)
                .verifyComplete();
    }

    @Test
    void transformWithMap_Length_repeat() {
        Flux<Integer> stringFlux = Flux.fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext(7, 7, 4, 7, 7, 4)
                .verifyComplete();
    }

    @Test
    void transformWithMapFilter() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .map(String::toLowerCase)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("florian", "yasmine")
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMap() {
        Flux<String> names = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                }).log();// for every element you make a db or external service call
        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMapUsingParallel() {
        Flux<String> names = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)//This produces a Flux<Flux<String>>
                .flatMap(stringFlux -> stringFlux.map(this::convertToList).subscribeOn(parallel()))// the parallel() is to make sure each map operation is carried out in parallel
                .flatMap(Flux::fromIterable)
                .log();


        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    void transformUsingFlatMapUsingParallelMaintainOrder() {
        Flux<String> names = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)//This produces a Flux<Flux<String>>
                //.concatMap(stringFlux -> stringFlux.map(this::convertToList).subscribeOn(parallel()))// concatMap() has the same function as flatMap() but maintains order
                .flatMapSequential(stringFlux -> stringFlux.map(this::convertToList).subscribeOn(parallel()))// concatMap() has the same function as flatMap() but maintains order
                .flatMap(Flux::fromIterable)
                .log();


        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }
}
