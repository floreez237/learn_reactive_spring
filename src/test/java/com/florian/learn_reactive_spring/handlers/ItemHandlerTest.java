package com.florian.learn_reactive_spring.handlers;

import com.florian.learn_reactive_spring.document.Item;
import com.florian.learn_reactive_spring.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.florian.learn_reactive_spring.constants.ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@ComponentScan(basePackages = {"com.florian.learn_reactive_spring.handlers", "com.florian.learn_reactive_spring.routers","com.florian.learn_reactive_spring.exception"})
public class ItemHandlerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    ItemRepository itemRepository;

    List<Item> itemList = Arrays.asList(new Item("ABc", "ITEL TV", 20_000),
            new Item("polar", "ITEL Phone", 45_000),
            new Item("a12", "Bag", 10_000),
            new Item("df232", "Dell Computer", 180000),
            new Item("ID", "ITEL", 20_000)
    );

    @Test
    void getAllItems() {
        Mockito.when(itemRepository.findAll()).thenReturn(Flux.fromIterable(itemList));

        webTestClient.get().uri(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/all"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .contains(new Item("ID", "ITEL", 20_000));
    }

    @Test
    void getAllItems2() {
        Mockito.when(itemRepository.findAll()).thenReturn(Flux.fromIterable(itemList));

        webTestClient.get().uri(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/all"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(listEntityExchangeResult -> {
                    List<Item> response = listEntityExchangeResult.getResponseBody();
                    assertNotNull(response);
                    for (Item item : response) {
                        assertNotNull(item.getId());
                    }
                });
    }

    @Test
    void getAllItems3() {
        Mockito.when(itemRepository.findAll()).thenReturn(Flux.fromIterable(itemList));

        Flux<Item> itemFlux = webTestClient.get().uri(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/all"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void getItemById() {
        Item itemToReturn = new Item("ID", "ITEL", 20_000);
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyString())).thenReturn(Mono.just(itemToReturn));

        webTestClient.get().uri(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), "ID")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .isEqualTo(itemToReturn);
    }

    @Test
    void getItemByIdNotFound() {
        Mockito.when(itemRepository.findById(ArgumentMatchers.anyString())).thenReturn(Mono.empty());

        webTestClient.get().uri(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/{id}"), "aID")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createItem() {
        Item toSave = new Item(null, "ITEL", 20_000);
        Item savedItem = new Item("ID", "ITEL", 20_000);
        Mockito.when(itemRepository.save(toSave)).thenReturn(Mono.just(savedItem));

        webTestClient.post().uri(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/new"))
                .body(Mono.just(toSave), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Item.class)
                .isEqualTo(savedItem);
    }

    @Test
    void deleteItem() {
        final String itemId = "ID";
        Mockito.when(itemRepository.existsById(ArgumentMatchers.anyString())).thenReturn(Mono.just(true));
        Mockito.when(itemRepository.deleteById(ArgumentMatchers.anyString())).thenReturn(Mono.empty());
        webTestClient
                .delete().uri(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/delete/{id}"), itemId)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(itemRepository).deleteById(itemId);
    }

    @Test
    void deleteItemNotFound() {
        final String itemId = "aID";
        Mockito.when(itemRepository.existsById(itemId)).thenReturn(Mono.just(false));
        webTestClient
                .delete().uri(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/delete/{id}"), itemId)
                .exchange()
                .expectStatus().isNotFound();

        Mockito.verify(itemRepository, never()).deleteById(ArgumentMatchers.anyString());
    }

    @Test
    void updateItem() {
        Item itemToUpdate = new Item("ID", "ITEL", 20_000);
        Mockito.when(itemRepository.existsById("ID")).thenReturn(Mono.just(true));
        Mockito.when(itemRepository.save(itemToUpdate)).thenReturn(Mono.just(itemToUpdate));

        webTestClient.put().uri(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/update"))
                .body(Mono.just(itemToUpdate), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .isEqualTo(itemToUpdate);
    }

    @Test
    void updateItemNotFound() {
        Item itemToUpdate = new Item("sID", "ITEL", 20_000);
        Mockito.when(itemRepository.existsById("sID")).thenReturn(Mono.just(false));

        webTestClient.put().uri(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/update"))
                .body(Mono.just(itemToUpdate), Item.class)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    void runtimeException() {
        webTestClient.get().uri("/fun/exception")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Thrown Exception");
//
    }
}
