package com.florian.learn_reactive_spring.intitializers;

import com.florian.learn_reactive_spring.document.AppUser;
import com.florian.learn_reactive_spring.document.Item;
import com.florian.learn_reactive_spring.document.ItemCapped;
import com.florian.learn_reactive_spring.repository.AppUserRepository;
import com.florian.learn_reactive_spring.repository.ItemReactiveCappedRepository;
import com.florian.learn_reactive_spring.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@Profile("!test")
public class ItemInitializer implements CommandLineRunner {
    private final ItemRepository itemRepository;
//    private final MongoOperations mongoOperations;
    private final ItemReactiveCappedRepository cappedRepository;

    @Autowired
    private  AppUserRepository appUserRepository;
    
    private final List<Item> itemList = Arrays.asList(new Item(null, "ITEL TV", 20_000),
            new Item(null, "ITEL Phone", 45_000),
            new Item(null, "Bag", 10_000),
            new Item(null, "Dell Computer", 180000),
            new Item("ID", "ITEL", 20_000)
    );

    public ItemInitializer(ItemRepository itemRepository, ItemReactiveCappedRepository cappedRepository) {
        this.itemRepository = itemRepository;
        this.cappedRepository = cappedRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemRepository::save)
                .subscribe(item -> System.out.println("Item: " + item));
        appUserRepository.deleteAll()
                .then(appUserRepository.insert(new AppUser(null, "florian", "lowe1", true))
                        .log("AppUser Inserted"))
                .subscribe();
//        createCappedCollection();

      /*  Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(aLong -> new ItemCapped(null,"Random Item " + aLong,100.00+aLong));
        cappedRepository.insert(itemCappedFlux)
                .subscribe(itemCapped -> log.info("Inserted Item is: {}",itemCapped));*/


    }
//
//    private void createCappedCollection() {
//        mongoOperations.dropCollection(ItemCapped.class);
//        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20)
//                .size(5000).capped());
//    }
}
