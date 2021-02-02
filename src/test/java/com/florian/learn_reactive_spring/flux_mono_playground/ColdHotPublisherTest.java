package com.florian.learn_reactive_spring.flux_mono_playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static reactor.core.scheduler.Schedulers.parallel;

public class ColdHotPublisherTest {
    @Test
    void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1)).log();

        stringFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));//emits value from beginning
        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));// emits value from beginning

        Thread.sleep(4000);

    }

    @Test
    void hotPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1)).log();

        final ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();// to make it behave as a hot publisher
        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));
        Thread.sleep(3000);

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));// does not emits value from beginning

        Thread.sleep(4000);
    }
}
