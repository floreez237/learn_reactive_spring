package com.florian.learn_reactive_spring.controllers.v1;

import com.florian.learn_reactive_spring.document.Item;
import com.florian.learn_reactive_spring.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.florian.learn_reactive_spring.constants.ItemConstants.ITEM_ENDPOINT_V1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@ActiveProfiles("test")
class ItemControllerTest {
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
        when(itemRepository.findAll()).thenReturn(Flux.fromIterable(itemList));
        webTestClient.get().uri(ITEM_ENDPOINT_V1 + "/all")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(listEntityExchangeResult -> {
                    List<Item> responseBody = listEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                    assertTrue(responseBody.stream().anyMatch(item -> item.getId().equals("ID")));
                });

    }

    @Test
    void getAllItems2() {
        when(itemRepository.findAll()).thenReturn(Flux.fromIterable(itemList));
        Mono<Boolean> hasElement = webTestClient.get().uri(ITEM_ENDPOINT_V1 + "/all")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Item.class)
                .getResponseBody()
                .filter(item -> item.getId().equals("ID"))
                .hasElements();

        StepVerifier.create(hasElement)
                .expectSubscription()
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findItemById() {
        when(itemRepository.findById(ArgumentMatchers.anyString())).
                thenReturn(Mono.just(new Item("ID", "ITEL", 20_000)));
        webTestClient.get().uri(ITEM_ENDPOINT_V1 + "/ID")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.description").isEqualTo("ITEL")
                .jsonPath("$.price").isEqualTo(20000);
    }

    @Test
    void findItemByIdNotFound() {
        when(itemRepository.findById(ArgumentMatchers.anyString())).
                thenReturn(Mono.empty());
        webTestClient.get().uri(ITEM_ENDPOINT_V1 + "/ID")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createItem() {
        Item toSave = new Item(null, "test", 200);
        Item savedItem = new Item("ID", "test", 200);
        when(itemRepository.save(toSave)).thenReturn(Mono.just(savedItem));

        webTestClient.post().uri(ITEM_ENDPOINT_V1.concat("/new"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(toSave), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Item.class)
                .isEqualTo(savedItem);
    }

    @Test
    void deleteItem() {
        String itemId = "ID";
        when(itemRepository.existsById(itemId)).thenReturn(Mono.just(true));
        when(itemRepository.deleteById(itemId)).thenReturn(Mono.empty());

        webTestClient.delete().uri(ITEM_ENDPOINT_V1.concat("/delete/{id}"), itemId)
                .exchange()
                .expectStatus().isOk();

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(itemRepository).existsById(argumentCaptor.capture());
        Mockito.verify(itemRepository).deleteById(itemId);

        assertEquals(itemId, argumentCaptor.getValue());


    }

    @Test
    void deleteItemNotFound() {
        String itemId = "Not Found";
        when(itemRepository.existsById(itemId)).thenReturn(Mono.just(false));

        webTestClient.delete().uri(ITEM_ENDPOINT_V1.concat("/delete/{id}"), itemId)
                .exchange()
                .expectStatus().isNotFound();

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(itemRepository).existsById(argumentCaptor.capture());
        Mockito.verify(itemRepository, never()).deleteById(itemId);

        assertEquals(itemId, argumentCaptor.getValue());
    }

    @Test
    void updateIem() {
        final String itemId = "ID";
        Item newItem = new Item(itemId, "update", 2000);
        when(itemRepository.existsById(itemId)).thenReturn(Mono.just(true));
        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(newItem));

        webTestClient.put().uri(ITEM_ENDPOINT_V1.concat("/update"))
                .body(Mono.just(newItem), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .isEqualTo(newItem);


        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(itemRepository).existsById(argumentCaptor.capture());
        Mockito.verify(itemRepository).save(newItem);

        assertEquals(itemId, argumentCaptor.getValue());
    }

    @Test
    void updateIemNotFound() {
        final String itemId = "IaD";
        Item newItem = new Item(itemId, "update", 2000);
        when(itemRepository.existsById(itemId)).thenReturn(Mono.just(false));

        webTestClient.put().uri(ITEM_ENDPOINT_V1.concat("/update"))
                .body(Mono.just(newItem), Item.class)
                .exchange()
                .expectStatus().isNotFound();

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(itemRepository).existsById(argumentCaptor.capture());
        Mockito.verify(itemRepository, never()).save(newItem);

        assertEquals(itemId, argumentCaptor.getValue());
    }

    @Test
    void runtimeException() {
        when(itemRepository.findAll()).thenReturn(Flux.empty());

        webTestClient.get().uri(ITEM_ENDPOINT_V1.concat("/exception"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Runtime Exception Occurred");

    }
}