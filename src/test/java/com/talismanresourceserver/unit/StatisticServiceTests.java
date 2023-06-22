package com.talismanresourceserver.unit;

import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.model.type.FightType;
import com.talismanresourceserver.repository.DeckRepository;
import com.talismanresourceserver.service.StatisticsService;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

        var result = statisticsService.getAllStats();

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

        var result = statisticsService.getUniqueStats();

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

        var result = statisticsService.getAllStatsFromDeck("Deck-one");

        StepVerifier.create(result)
                .consumeNextWith(deckStatisticsDTO -> {
                    Assertions.assertThat(deckStatisticsDTO).isNotNull();
                    Assertions.assertThat(deckStatisticsDTO.getNumberOfCards()).isEqualTo(5);
                    Assertions.assertThat(deckStatisticsDTO.getNumberOfEnemies()).isEqualTo(4);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnStatisticsFromUniqueFromSpecificDeck() {
        given(deckRepository.findDeckByNameOfDeck("Deck-one")).willReturn(Mono.just(decks.get(0)));

        var result = statisticsService.getUniqueStatsFromDeck("Deck-one");

        StepVerifier.create(result)
                .consumeNextWith(deckStatisticsDTO -> {
                    Assertions.assertThat(deckStatisticsDTO).isNotNull();
                    Assertions.assertThat(deckStatisticsDTO.getNumberOfCards()).isEqualTo(3);
                    Assertions.assertThat(deckStatisticsDTO.getNumberOfEnemies()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnDeckEnemiesStats() {
        var cards = List.of(
                Card.builder().name("enemy-one").type(CardType.WRÓG).fight_statistic(FightType.SIŁA).fight_power("2").number_of_copies(3).build(),
                Card.builder().name("enemy-two").type(CardType.WRÓG).fight_statistic(FightType.MOC).fight_power("7").number_of_copies(1).build(),
                Card.builder().name("enemy-two").type(CardType.WRÓG).fight_statistic(FightType.MOC).fight_power("?").number_of_copies(1).build(),
                Card.builder().name("enemy-one").type(CardType.WRÓG).fight_statistic(FightType.SIŁA_MOC).fight_power("5").number_of_copies(5).build()
        );

        given(deckRepository.findCardsOfTypeInDeck(anyString(), eq(CardType.WRÓG))).willReturn(Mono.just(Deck.builder().cards(cards).build()));

        var result = statisticsService.getAllEnemiesFromDeck("deck");

        StepVerifier.create(result)
                .consumeNextWith(deckEnemiesStatsDTO -> {
                    Assertions.assertThat(deckEnemiesStatsDTO).isNotNull();
                    Assertions.assertThat(deckEnemiesStatsDTO.getNumberOfEnemies()).isEqualTo(10);
                    Assertions.assertThat(deckEnemiesStatsDTO.getNumberOfEnemiesWithStrength()).isEqualTo(8);
                    Assertions.assertThat(deckEnemiesStatsDTO.getNumberOfEnemiesWithPower()).isEqualTo(7);
                    Assertions.assertThat(deckEnemiesStatsDTO.getMaxEnemyStrength()).isEqualTo(5);
                    Assertions.assertThat(deckEnemiesStatsDTO.getMinEnemyStrength()).isEqualTo(2);
                    Assertions.assertThat(deckEnemiesStatsDTO.getMaxEnemyPower()).isEqualTo(7);
                    Assertions.assertThat(deckEnemiesStatsDTO.getMinEnemyPower()).isEqualTo(5);
                    Assertions.assertThat(deckEnemiesStatsDTO.getAverageEnemyStrength()).isEqualTo(3.875);
                    Assertions.assertThat(deckEnemiesStatsDTO.getAverageEnemyPower()).isCloseTo(5.333333, Offset.offset(1.0));

                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEnemiesByPower() {
        var cards = List.of(
                Card.builder().name("enemy-one").type(CardType.WRÓG).fight_statistic(FightType.SIŁA).fight_power("2").number_of_copies(3).build(),
                Card.builder().name("enemy-two").type(CardType.WRÓG).fight_statistic(FightType.MOC).fight_power("7").number_of_copies(1).build(),
                Card.builder().name("enemy-two").type(CardType.WRÓG).fight_statistic(FightType.MOC).fight_power("?").number_of_copies(1).build(),
                Card.builder().name("enemy-one").type(CardType.WRÓG).fight_statistic(FightType.SIŁA_MOC).fight_power("2").number_of_copies(5).build()
        );

        given(deckRepository.findCardsOfTypeInDeck(anyString(), eq(CardType.WRÓG))).willReturn(Mono.just(Deck.builder().cards(cards).build()));

        var result = statisticsService.getNumberOfEnemies("deck");

        StepVerifier.create(result)
                .consumeNextWith(response -> {

                    Map<FightType, Integer> map = new HashMap<>();
                    map.put(FightType.SIŁA, 3);
                    map.put(FightType.SIŁA_MOC, 5);

                })
                .verifyComplete();
    }
}
