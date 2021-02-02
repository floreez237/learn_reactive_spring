package com.florian.learn_reactive_spring.repository;

import com.florian.learn_reactive_spring.document.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped, String> {
    @Tailable
    Flux<ItemCapped> findItemCappedBy();
}
