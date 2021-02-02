package com.florian.learn_reactive_spring.flux_mono_playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxMonoWithTimeTest {
    @Test
    void infiniteSequence() throws InterruptedException {
        /*Here the interval method will generate numbers as long as the thread in which it is executing is active
         * if we remove the sleep(3000) at the end, the thread will die and not will be generated from the flux
         * */
        Flux<Long> infiniteLongFlux = Flux.interval(Duration.ofMillis(200))
                .log();
        infiniteLongFlux.subscribe(aLong -> System.out.println("Value is " + aLong));
        Thread.sleep(3000);

    }

    @Test
    void infiniteSequenceTest() throws InterruptedException {
        /*Here the interval method will generate numbers as long as the thread in which it is executing is active
         * if we remove the sleep(3000) at the end, the thread will die and not will be generated from the flux
         * */
        Flux<Long> finiteLongFlux = Flux.interval(Duration.ofMillis(200))
                .take(3)
                .log();
        StepVerifier.create(finiteLongFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void infiniteSequenceMapperTest() throws InterruptedException {
        /*Here the interval method will generate numbers as long as the thread in which it is executing is active
         * if we remove the sleep(3000) at the end, the thread will die and not will be generated from the flux
         * */
        Flux<Integer> finiteLongFlux = Flux.interval(Duration.ofMillis(200))
                .map(Long::intValue)
                .take(3)
                .log();
        StepVerifier.create(finiteLongFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void infiniteSequenceMapper_withDelayTest() throws InterruptedException {
        /*Here the interval method will generate numbers as long as the thread in which it is executing is active
         * if we remove the sleep(3000) at the end, the thread will die and not will be generated from the flux
         * */
        Flux<Integer> finiteLongFlux = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(Long::intValue)
                .take(3)
                .log();
        StepVerifier.create(finiteLongFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

}
