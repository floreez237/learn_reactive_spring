package com.florian.learn_reactive_spring.controllers.v1;

import com.florian.learn_reactive_spring.document.Item;
import com.florian.learn_reactive_spring.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.florian.learn_reactive_spring.constants.ItemConstants.ITEM_ENDPOINT_V1;

@RestController
@Slf4j
@RequestMapping(ITEM_ENDPOINT_V1)
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/all")
    public Flux<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Item>> getItem(@PathVariable("id") String id) {
        return itemRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Item>> createItem(@RequestBody Item item) {
        return itemRepository.save(item)
                .map(savedItem -> new ResponseEntity<Item>(savedItem, HttpStatus.CREATED))
                .onErrorReturn(IllegalStateException.class, new ResponseEntity<>(HttpStatus.NO_CONTENT));

    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Item>> deleteItem(@PathVariable String id) {
        return itemRepository.existsById(id)
                .map(isExists -> {
                    if (isExists) {
                        itemRepository.deleteById(id).subscribe();
                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                });
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<Item>> updateItem(@RequestBody Item item) {
        return itemRepository.existsById(item.getId())
                .map(exists -> {
                    if (exists) {
                        itemRepository.save(item).subscribe();
                        return new ResponseEntity<>(item, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                });
    }

    @GetMapping("/exception")
    public Flux<Item> runtimeException() {
        return itemRepository.findAll()
                .concatWith(Flux.error(new RuntimeException("Runtime Exception Occurred")));
    }



}
