package com.florian.learn_reactive_spring.flux_mono_playground;

import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxMonoFilterTest {
    List<String> names = Arrays.asList("Florian", "Yasmine", "Lowe");

    @Test
    void filterTest() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(s -> s.contains("e"))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Yasmine", "Lowe")
                .verifyComplete();
    }

    @Test
    void filterTestLength() {
        Flux<String> stringFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Florian", "Yasmine")
                .verifyComplete();
    }
}

