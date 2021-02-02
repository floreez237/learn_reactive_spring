package com.florian.learn_reactive_spring.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class FluxMonoControllerTest {

    @Autowired
    WebTestClient webClient;


    @Test
    void integerFlux() {
//        VirtualTimeScheduler.getOrSet();
        Flux<Integer> integerFlux = webClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()//to invoke the endpoint
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(4))
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void integerFluxApproach2() {
        webClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()//to invoke the endpoint
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    void integerFluxApproach3() {
        List<Integer> expected = Arrays.asList(1, 2, 3, 4);

        EntityExchangeResult<List<Integer>> listExchangeResult = webClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()//to invoke the endpoint
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        assertEquals(expected, listExchangeResult.getResponseBody());
    }

    @Test
    void integerFluxApproach4() {
        List<Integer> expected = Arrays.asList(1, 2, 3, 4);

        webClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()//to invoke the endpoint
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(listEntityExchangeResult -> assertEquals(expected, listEntityExchangeResult.getResponseBody()));


    }

    @Test
    void fluxStreamTest() {
        Flux<Long> integerFlux = webClient.get().uri("/fluxstream")
                .accept(MediaType.valueOf(MediaType.APPLICATION_STREAM_JSON_VALUE))
                .exchange()//to invoke the endpoint
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(0l, 1l, 2l, 3l)
                .thenCancel()
                .verify();
    }

    @Test
    void monoTest() {
        Mono<Integer> integerMono = webClient.get().uri("/mono")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody()
                .single();

        StepVerifier.create(integerMono)
                .expectSubscription()
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void monoTestApproach2() {
        Integer integer = webClient.get().uri("/mono")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .returnResult()
                .getResponseBody();

        assertEquals(1, integer);
    }

    @Test
    void monoTestApproach3() {
        webClient.get().uri("/mono")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(integerEntityExchangeResult -> assertEquals(1, integerEntityExchangeResult.getResponseBody()));

    }
}