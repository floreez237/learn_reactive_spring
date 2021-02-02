package com.florian.learn_reactive_spring.controllers.v1;

import com.florian.learn_reactive_spring.document.ItemCapped;
import com.florian.learn_reactive_spring.repository.ItemReactiveCappedRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.florian.learn_reactive_spring.constants.ItemConstants.ITEM_STREAM_ENDPOINT_V1;

@RestController
@RequestMapping(ITEM_STREAM_ENDPOINT_V1)
public class ItemStreamController {
    private final ItemReactiveCappedRepository itemReactiveCappedRepository;

    public ItemStreamController(ItemReactiveCappedRepository cappedRepository) {
        this.itemReactiveCappedRepository = cappedRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemCappedStream() {
        return itemReactiveCappedRepository.findItemCappedBy();
    }
}
