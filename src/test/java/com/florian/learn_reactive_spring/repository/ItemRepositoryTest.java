package com.florian.learn_reactive_spring.repository;

import com.florian.learn_reactive_spring.document.Item;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    List<Item> itemList = Arrays.asList(new Item(null, "ITEL TV", 20_000),
            new Item(null, "ITEL Phone", 45_000),
            new Item(null, "Bag", 10_000),
            new Item(null, "Dell Computer", 180000),
            new Item("ID", "ITEL", 20_000)
    );

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemRepository::save)
                .doOnNext(item -> System.out.println("Item: " + item))
                .log()
                .blockLast();//this will wait for the pipeline to run completely before doing anything else
        //it should only be used in tests
    }

    @Test
    void getAllItems() {
        StepVerifier.create(itemRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void getItemByID() {
        Mono<Item> itemMono = itemRepository.findById("ID").log();

        StepVerifier.create(itemMono)
                .expectSubscription()
                .expectNext(new Item("ID", "ITEL", 20_000))
                .verifyComplete();
    }

    @Test
    void findByDescription() {
        Flux<Item> itemFlux = itemRepository.findAllByDescription("ITEL")
                .log();
        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextMatches(item -> Double.compare(20_000, item.getPrice()) == 0)
                .verifyComplete();
    }

    @Test
    void save() {
        Item item = new Item(null, "test", 200);
        StepVerifier.create(itemRepository.save(item))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null)
                .verifyComplete();

        StepVerifier.create(itemRepository.count())
                .expectSubscription()
                .expectNext(6L)
                .verifyComplete();
    }

    @Test
    void delete() {
        StepVerifier.create(itemRepository.deleteById("ID"))
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemRepository.count())
                .expectSubscription()
                .expectNext(4L)
                .verifyComplete();
    }

    @Test
    void update() {
        Mono<Item> updatedItem = itemRepository.findById("IaD");
               /* .map(item -> {
                    item.setDescription("new");
                    return item;
                })
                .flatMap(item -> itemRepository.save(item))
                .log();

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("new"))
                .verifyComplete();*/

        StepVerifier.create(updatedItem.hasElement())
                .expectSubscription()
                .expectNext(false)
                .verifyComplete();

    }
}