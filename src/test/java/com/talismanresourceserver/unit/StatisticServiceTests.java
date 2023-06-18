package com.talismanresourceserver.unit;

import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.repository.DeckRepository;
import com.talismanresourceserver.service.StatisticsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StatisticServiceTests {

    @Mock
    private DeckRepository deckRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    private static List<Deck> decks;

    @BeforeAll
    static void load() {
        var card1 = Card.builder().name("Enemy-Strength-1").type(CardType.WRÓG).number_of_copies(3).build();
        var card2 = Card.builder().name("Enemy-Strength-2").type(CardType.WRÓG).number_of_copies(1).build();
        var card3 = Card.builder().name("Place").type(CardType.MIEJSCE).number_of_copies(1).build();

        var deck1 = Deck.builder().nameOfDeck("Deck-one").cards(List.of(card1, card2, card3)).build();
        var deck2 = Deck.builder().nameOfDeck("Deck-two").cards(List.of(card1, card3)).build();

        decks = List.of(deck1, deck2);
    }

    @Test
    void shouldReturnStatisticsFromAllDecks() {
        given(deckRepository.findAll()).willReturn(Flux.fromIterable(decks));
        given(deckRepository.findDeckByNameOfDeck("Deck-one")).willReturn(Mono.just(decks.get(0)));
        given(deckRepository.findDeckByNameOfDeck("Deck-two")).willReturn(Mono.just(decks.get(1)));

        var result = statisticsService.getBasicStatisticsFromAllCards();

        StepVerifier.create(result)
                .consumeNextWith(deckStatisticsDTO -> {
                    Assertions.assertThat(deckStatisticsDTO).isNotNull();
                    Assertions.assertThat(deckStatisticsDTO.getNumberOfEnemies()).isEqualTo(7);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnStatisticsFromUniqueDecks() {
        given(deckRepository.findAll()).willReturn(Flux.fromIterable(decks));
        given(deckRepository.findDeckByNameOfDeck("Deck-one")).willReturn(Mono.just(decks.get(0)));
        given(deckRepository.findDeckByNameOfDeck("Deck-two")).willReturn(Mono.just(decks.get(1)));

        var result = statisticsService.getBasicStatisticsFromUniqueCards();

        StepVerifier.create(result)
                .consumeNextWith(deckStatisticsDTO -> {
                    Assertions.assertThat(deckStatisticsDTO).isNotNull();
                    Assertions.assertThat(deckStatisticsDTO.getNumberOfEnemies()).isEqualTo(3);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnStatisticsFromAllFromSpecificDeck() {
        given(deckRepository.findDeckByNameOfDeck("Deck-one")).willReturn(Mono.just(decks.get(0)));

        var result = statisticsService.getBasicStatisticsFromAllCardsOfDeck("Deck-one");

        StepVerifier.create(result)
                .consumeNextWith(deckStatisticsDTO -> {
                    Assertions.assertThat(deckStatisticsDTO).isNotNull();
                    Assertions.assertThat(deckStatisticsDTO.getNumberOfCards()).isEqualTo(5);
                    Assertions.assertThat(deckStatisticsDTO.getNumberOfEnemies()).isEqualTo(3);
                });
    }

    @Test
    void shouldReturnStatisticsFromUniqueFromSpecificDeck() {
        given(deckRepository.findDeckByNameOfDeck("Deck-one")).willReturn(Mono.just(decks.get(0)));

        var result = statisticsService.getBasicStatisticsFromUniqueCardsOfDeck("Deck-one");

        StepVerifier.create(result)
                .consumeNextWith(deckStatisticsDTO -> {
                    Assertions.assertThat(deckStatisticsDTO).isNotNull();
                    Assertions.assertThat(deckStatisticsDTO.getNumberOfCards()).isEqualTo(3);
                    Assertions.assertThat(deckStatisticsDTO.getNumberOfEnemies()).isEqualTo(1);
                });
    }
}
