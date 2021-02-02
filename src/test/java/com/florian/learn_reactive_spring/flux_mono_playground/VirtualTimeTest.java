package com.florian.learn_reactive_spring.flux_mono_playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {
    @Test
    void withoutVirtualTime() {
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3).log();

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }

    @Test
    void withVirtualTime() {
        VirtualTimeScheduler.getOrSet();// this must be placed at the beginning
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3).log();

        StepVerifier.withVirtualTime(() -> longFlux)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))//this is the key to speed up the process
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }
}
