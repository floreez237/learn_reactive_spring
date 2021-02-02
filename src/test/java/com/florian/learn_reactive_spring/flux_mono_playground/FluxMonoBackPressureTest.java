package com.florian.learn_reactive_spring.flux_mono_playground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxMonoBackPressureTest {
    @Test
    void backPressureTest() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(2)
                .expectNext(2, 3)
                .thenCancel()
                .verify();
    }

    @Test
    void backPressure() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();
        integerFlux.subscribe(integer -> System.out.println("element is " + integer),
                System.err::println,
                () -> System.out.println("Complete"),
                subscription -> subscription.request(2));
    }

    @Test
    void backPressure_cancel() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();
        integerFlux.subscribe(integer -> System.out.println("element is " + integer),
                System.err::println,
                () -> System.out.println("Complete"),
                Subscription::cancel);
    }

    @Test
    void customBackPressure() {
        Flux<Integer> integerFlux = Flux.range(1, 10)
                .log();
        integerFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value received is: " + value);
                if (value == 4) {
                    cancel();
                }
            }
        });
    }
}
