package com.florian.learn_reactive_spring.handlers;

import com.florian.learn_reactive_spring.document.Item;
import com.florian.learn_reactive_spring.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.swing.plaf.basic.BasicComboBoxUI;
import java.sql.SQLOutput;

@Component
@Slf4j
public class ItemHandler {

    private final ItemRepository itemRepository;

    public ItemHandler(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRepository.findAll(), Item.class);
                /*.body(itemRepository.findAll().concatWith(Mono.error(new RuntimeException("All Items Exception"))), Item.class)
                .onErrorResume(throwable -> ServerResponse.badRequest()
                        .body(throwable.getMessage(),String.class));*/
    }

    public Mono<ServerResponse> getItemById(ServerRequest serverRequest) {
        String itemId = serverRequest.pathVariable("id");

        return itemRepository.findById(itemId)
                .flatMap(item -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(item), Item.class))
                .switchIfEmpty(ServerResponse.notFound().build());

    }


    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
        return itemMono.log("Create Item Log").flatMap(itemRepository::save)
                .flatMap(item -> ServerResponse.status(HttpStatus.CREATED)
                        .body(Mono.just(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        String itemId = serverRequest.pathVariable("id");
        return itemRepository.existsById(itemId)
                .flatMap(exists -> {
                    if (exists) {
                        itemRepository.deleteById(itemId).subscribe();
                        return ServerResponse.ok().build();
                    } else {
                        return ServerResponse.notFound().build();
                    }
                });
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
        return itemMono.flatMap(item -> itemRepository.existsById(item.getId())
                .log("Second Log")
                .flatMap(exists -> {
                    if (exists) {
                        itemRepository.save(item).subscribe();
                        return ServerResponse.ok().body(Mono.just(item), Item.class);
                    } else {
                        return ServerResponse.notFound().build();
                    }
                }));
    }

    public Mono<ServerResponse> itemEx(ServerRequest serverRequest) {
        throw new RuntimeException("Thrown Exception");
    }
}
