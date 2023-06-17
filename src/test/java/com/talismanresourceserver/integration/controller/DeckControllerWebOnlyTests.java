package com.talismanresourceserver.integration.controller;

import com.talismanresourceserver.controller.DeckStatisticsController;
import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.model.type.FightType;
import com.talismanresourceserver.service.CardService;
import com.talismanresourceserver.service.StatisticsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@WebFluxTest()
public class DeckControllerWebOnlyTests{

    @MockBean
    private CardService cardService;

    @MockBean
    private DeckStatisticsController deckStatisticsController;

    @MockBean
    private StatisticsService statisticsService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithMockUser
    void shouldReturnDecks() {

        var decks = List.of(
                Deck.builder().nameOfDeck("first-deck").build(),
                Deck.builder().nameOfDeck("second-deck").build()
        );

        given(cardService.getAllDecks()).willReturn(Flux.fromIterable(decks));

        webTestClient.get()
                .uri("/api")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Deck.class)
                .consumeWith(response -> {

                    var body = response.getResponseBody();

                    Assertions.assertThat(body).isNotNull();
                    Assertions.assertThat(body.size()).isEqualTo(2);
                    Assertions.assertThat(body).containsAll(decks);
                });
    }

    @Test
    @WithMockUser
    void shouldReturnDeckByName() {
        var deck = Deck.builder().nameOfDeck("test-deck").build();

        given(cardService.getDeckByNameOfDeck("test-deck")).willReturn(Mono.just(deck));

        webTestClient.get()
                .uri("/api/test-deck")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Deck.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();

                    Assertions.assertThat(body).isNotNull();
                    Assertions.assertThat(body).isEqualTo(deck);
                });
    }

    @Test
    @WithMockUser
    void shouldReturnCardFromDeckByName() {

        var card = Card.builder().name("name-of-card").build();

        given(cardService.getCardFromDeckByName(anyString(), eq("name-of-card")))
                .willReturn(Mono.just(card));

        webTestClient.get()
                .uri("/api/test/name-of-card")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Card.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();

                    Assertions.assertThat(body).isNotNull();
                    Assertions.assertThat(body).isEqualTo(card);
                });
    }

    @Test
    @WithMockUser
    void shouldReturnCardsOfTypeFromDeck() {

        var cards = List.of(
                Card.builder().name("follower-one").type(CardType.PRZYJACIEL).build(),
                Card.builder().name("follower-two").type(CardType.PRZYJACIEL).build()
        );

        given(cardService.getCardsFromTypeInDeck(anyString(), eq(CardType.PRZYJACIEL)))
                .willReturn(Flux.fromIterable(cards));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/api/test").queryParam("type", CardType.PRZYJACIEL).build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Card.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();

                    Assertions.assertThat(body).isNotNull();
                    Assertions.assertThat(body.size()).isEqualTo(2);
                    Assertions.assertThat(body).containsAll(cards);
                });
    }

    @Test
    @WithMockUser
    void shouldReturnEnemies() {
        var cards = List.of(
                Card.builder().name("enemy-one").type(CardType.WRÓG).fight_statistic(FightType.MOC).build(),
                Card.builder().name("enemy-two").type(CardType.WRÓG).fight_statistic(FightType.MOC).build()
        );

        given(cardService.getEnemiesFromDeckByStatisticType(anyString(), eq(FightType.MOC)))
                .willReturn(Flux.fromIterable(cards));

        webTestClient.get()
                .uri("/api/test/enemies/MOC")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Card.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();

                    Assertions.assertThat(body).isNotNull();
                    Assertions.assertThat(body.size()).isEqualTo(2);
                    Assertions.assertThat(body).containsAll(cards);
                });
    }
}
