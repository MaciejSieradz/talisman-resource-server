package com.talismanresourceserver.unit;

import com.talismanresourceserver.exception.CardNotFoundException;
import com.talismanresourceserver.exception.DeckNotFoundException;
import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.model.type.FightType;
import com.talismanresourceserver.repository.DeckRepository;
import com.talismanresourceserver.service.CardService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CardServiceTests {

    @Mock
    private DeckRepository deckRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void shouldReturnAllDecks() {
        var deck_one = Deck.builder().nameOfDeck("test-deck-one").build();
        var deck_two = Deck.builder().nameOfDeck("test=deck-two").build();

        given(deckRepository.findAll()).willReturn(Flux.just(deck_one, deck_two));

        var result = cardService.getAllDecks();

        StepVerifier.create(result)
                .expectNext(deck_one)
                .expectNext(deck_two)
                .verifyComplete();
    }

    @Test
    void shouldReturnDeck() {

        var deck = Deck.builder().nameOfDeck("test-deck").build();

        given(deckRepository.findDeckByNameOfDeck("test-deck")).willReturn(Mono.just(deck));

        var result = cardService.getDeckByNameOfDeck("test-deck");

        StepVerifier.create(result)
                .consumeNextWith(response -> {
                    Assertions.assertThat(response).isNotNull();
                    Assertions.assertThat(response).isEqualTo(deck);
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowDeckNotFoundException() {
        given(deckRepository.findDeckByNameOfDeck(anyString())).willReturn(Mono.empty());

        var result = cardService.getDeckByNameOfDeck("wrong");

        StepVerifier.create(result)
                .expectError(DeckNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnCard() {

        var card = Card.builder().name("test-card").build();

        given(deckRepository.findCardInDeck(anyString(), anyString()))
                .willReturn(Mono.
                        just(Deck.builder()
                                .cards(List.of(Card.builder().name("test-card").build())).build()));

        var result = cardService.getCardFromDeckByName("name-deck", "test-card");

        StepVerifier.create(result)
                .expectNext(card)
                .verifyComplete();
    }

    @Test
    void shouldThrowCardNotFoundException() {
        given(deckRepository.findCardInDeck(anyString(), anyString())).willReturn(Mono.empty());

        var result = cardService.getCardFromDeckByName("name-of-deck", "name-of-card");

        StepVerifier.create(result)
                .expectError(CardNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnPlaceCardsFromDeck() {

        var cards = List.of(
                Card.builder().name("lake").type(CardType.MIEJSCE).build(),
                Card.builder().name("forrest").type(CardType.MIEJSCE).build());

        given(deckRepository.findCardsOfTypeInDeck(anyString(), eq(CardType.MIEJSCE)))
                .willReturn(Mono.just(Deck.builder().cards(cards).build()));

        var result = cardService.getCardsFromTypeInDeck("name-of-deck",
                CardType.MIEJSCE);

        StepVerifier.create(result)
                .expectNext(cards.get(0))
                .expectNext(cards.get(1))
                .verifyComplete();
    }

    @Test
    void shouldReturnEnemiesFromDeck() {
        var cards = List.of(
                Card.builder().name("enemy-one").type(CardType.WRÓG).fight_statistic(FightType.MOC).build(),
                Card.builder().name("enemy-two").type(CardType.WRÓG).fight_statistic(FightType.MOC).build()
                );

        given(deckRepository.findEnemiesInDeckByStatistic(anyString(), eq(FightType.MOC)))
                .willReturn(Mono.just(Deck.builder().cards(cards).build()));

        var result = cardService.getEnemiesFromDeckByStatisticType("name-of-deck", FightType.MOC);

        StepVerifier.create(result)
                .expectNext(cards.get(0))
                .expectNext(cards.get(1))
                .verifyComplete();
    }

}
