package com.florian.learn_reactive_spring.handlers;

import com.florian.learn_reactive_spring.document.ItemCapped;
import com.florian.learn_reactive_spring.repository.ItemReactiveCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ItemCappedHandler {

    private final ItemReactiveCappedRepository itemReactiveCappedRepository;

    public ItemCappedHandler(ItemReactiveCappedRepository itemReactiveCappedRepository) {
        this.itemReactiveCappedRepository = itemReactiveCappedRepository;
    }

    public Mono<ServerResponse> cappedStream(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(BodyInserters.fromPublisher(itemReactiveCappedRepository.findItemCappedBy(), ItemCapped.class));
    }
}
