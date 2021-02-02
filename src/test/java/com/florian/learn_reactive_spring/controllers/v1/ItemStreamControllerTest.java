/*
package com.florian.learn_reactive_spring.controllers.v1;

import com.florian.learn_reactive_spring.constants.ItemConstants;
import com.florian.learn_reactive_spring.document.ItemCapped;
import com.florian.learn_reactive_spring.repository.ItemReactiveCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemStreamControllerTest {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(100))
                .map(aLong -> new ItemCapped(null, "Random Item " + aLong, 100.00 + aLong))
                .take(5);
        itemReactiveCappedRepository.insert(itemCappedFlux)
                .doOnNext(itemCapped -> System.out.printf("Inserted Item is: %s\n", itemCapped))
                .blockLast();
    }

    @Test
    void getItemCappedStream() {
        Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri(ItemConstants.ITEM_STREAM_ENDPOINT_V1)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);// this is because it is an infinite stream

        StepVerifier.create(itemCappedFlux)
                .expectSubscription()
                .expectNextCount(5L)
                .thenCancel();
    }
}*/
