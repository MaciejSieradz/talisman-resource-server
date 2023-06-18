package com.talismanresourceserver.integration.controller;

import com.talismanresourceserver.dto.ExceptionResponseDTO;
import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.model.type.FightType;
import com.talismanresourceserver.repository.DeckRepository;
import com.talismanresourceserver.service.CardService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
public class DeckControllerTests {
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private CardService cardService;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private WebTestClient webTestClient;
    private static List<Deck> decks;
    private static Card enemyCard;
    private static Card placeCard;

    @BeforeAll
    static void load() {
        enemyCard = Card.builder().name("enemy").type(CardType.WRÓG).fight_statistic(FightType.SIŁA).build();
        placeCard = Card.builder().name("place").type(CardType.MIEJSCE).build();

        decks = List.of(Deck.builder().nameOfDeck("test-name-one").cards(List.of(enemyCard, placeCard)).build(),
                Deck.builder().nameOfDeck("test-name-two").cards(List.of(enemyCard, placeCard)).build());


    }

    @BeforeEach
    void saveData() {
        deckRepository.saveAll(decks).collectList().block();
    }

    @Test
    void shouldReturnDecks() {
        webTestClient.get()
                .uri("/api/decks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Deck.class)
                .consumeWith(resposne -> {
                    var body = resposne.getResponseBody();

                    Assertions.assertThat(body).isNotNull();
                    Assertions.assertThat(body.size()).isEqualTo(2);
                    Assertions.assertThat(body).containsAll(decks);
                });
    }

    @Test
    void shouldReturnDeckErrorResponse() {
        webTestClient.get()
                .uri("/api/decks/invalid-deck-name")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ExceptionResponseDTO.class)
                .consumeWith(response -> {

                    var body = response.getResponseBody();

                    String expectedMessage = "Deck: invalid-deck-name not found!";

                    Assertions.assertThat(body.getMessage()).isEqualTo(expectedMessage);
                    Assertions.assertThat(body.getStatus()).isEqualTo(404);
                });
    }
}
