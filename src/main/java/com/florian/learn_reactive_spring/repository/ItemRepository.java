package com.florian.learn_reactive_spring.repository;

import com.florian.learn_reactive_spring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

    Flux<Item> findAllByDescription(String description);

}
