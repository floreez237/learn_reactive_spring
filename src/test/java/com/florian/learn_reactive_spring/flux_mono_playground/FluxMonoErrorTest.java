package com.florian.learn_reactive_spring.flux_mono_playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

public class FluxMonoErrorTest {
    @Test
    void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("D"))
                .onErrorResume(e -> {
                    System.out.println("Exception is" + e);
                    return Flux.just("default", "default2");
                })
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNextCount(3)
                .expectNext("default", "default2")
                .verifyComplete();
    }


    @Test
    void fluxErrorHandling_OnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default")
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNextCount(3)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void fluxErrorHandling_OnErrorMap() {//the error signal is mapped from one error class to another
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNextCount(3)
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    void fluxErrorHandling_OnErrorMap_withRetry() {//the error signal is mapped from one error class to another
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
                .retry(2)
                .log();
        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNextCount(9)
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    void fluxErrorHandling_OnErrorMap_withRetryBackoff() {//the error signal is mapped from one error class to another
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
                .retryWhen(RetrySpec.backoff(1, Duration.ofSeconds(5)))
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNextCount(6)
                .expectError(IllegalStateException.class)
                .verify();
    }

}
