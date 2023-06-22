package com.talismanresourceserver.service;

import com.talismanresourceserver.dto.DeckEnemiesDTO;
import com.talismanresourceserver.dto.DeckEnemiesStatsDTO;
import com.talismanresourceserver.dto.DeckStatisticsDTO;
import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.model.type.FightType;
import com.talismanresourceserver.repository.DeckRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final DeckRepository deckRepository;

    private enum TypeOfStatistics {UNIQUE, ALL}

    public Mono<DeckStatisticsDTO> getUniqueStats() {
        return deckRepository.findAll().flatMap(deck -> getUniqueStatsFromDeck(deck.getNameOfDeck()))
                .collectList().flatMap(this::createStatsFromAllDecks);
    }

    public Mono<DeckStatisticsDTO> getAllStats() {
        return deckRepository.findAll().flatMap(deck -> getAllStatsFromDeck(deck.getNameOfDeck()))
                .collectList().flatMap(this::createStatsFromAllDecks);
    }

    public Mono<DeckStatisticsDTO> getUniqueStatsFromDeck(String nameOfDeck) {
        return deckRepository.findDeckByNameOfDeck(nameOfDeck).map(Deck::getCards)
                .flatMap(cards -> createDeckStatisticsFromCards(cards, TypeOfStatistics.UNIQUE));
    }

    public Mono<DeckStatisticsDTO> getAllStatsFromDeck(String nameOfDeck) {
        return deckRepository.findDeckByNameOfDeck(nameOfDeck).map(Deck::getCards)
                .flatMap(cards -> createDeckStatisticsFromCards(cards, TypeOfStatistics.ALL));
    }

    public Mono<DeckEnemiesStatsDTO> getAllEnemiesFromDeck(String nameOfDeck) {

        return deckRepository.findCardsOfTypeInDeck(nameOfDeck, CardType.WRÓG)
                .map(Deck::getCards).map(cards -> {

                    int numberOfEnemiesTotal = cards.stream().mapToInt(Card::getNumber_of_copies).sum();
                    int numberOfStrengthEnemies = numberOfEnemiesFromType(cards, FightType.SIŁA);
                    int numberOfPowerEnemies = numberOfEnemiesFromType(cards, FightType.MOC);

                    int maxStrength = maxValueFromType(cards, FightType.SIŁA);
                    int maxPower = maxValueFromType(cards, FightType.MOC);

                    int minStrength = minValueFromType(cards, FightType.SIŁA);
                    int minPower = minValueFromType(cards, FightType.MOC);

                    double averageStrength = averageValueFromType(cards, FightType.SIŁA);
                    double averagePower = averageValueFromType(cards, FightType.MOC);

                    return DeckEnemiesStatsDTO.builder()
                            .numberOfEnemies(numberOfEnemiesTotal)
                            .numberOfEnemiesWithStrength(numberOfStrengthEnemies)
                            .numberOfEnemiesWithPower(numberOfPowerEnemies)
                            .averageEnemyStrength(averageStrength)
                            .maxEnemyStrength(maxStrength)
                            .minEnemyStrength(minStrength)
                            .averageEnemyPower(averagePower)
                            .maxEnemyPower(maxPower)
                            .minEnemyPower(minPower)
                            .build();
                });
    }

    public Flux<DeckEnemiesDTO> getNumberOfEnemies(String nameOfDeck) {

        return this.deckRepository.findCardsOfTypeInDeck(nameOfDeck, CardType.WRÓG).map(Deck::getCards)
                .flatMapIterable(this::createSetOfValues);
    }

    private List<DeckEnemiesDTO> createSetOfValues(List<Card> cards) {

        Set<String> values = cards.stream().map(Card::getFight_power).collect(toSet());

        var enemies = new ArrayList<DeckEnemiesDTO>();

        values.forEach(value -> {

            var map = returnMapFromString(value, cards);

            int numberOfStrengthEnemies = map.getOrDefault(FightType.SIŁA, 0);
            int numberOfPowerEnemies = map.getOrDefault(FightType.MOC,0);
            int numberOfStrengthOrPowerEnemies = map.getOrDefault(FightType.SIŁA_MOC,0);

            enemies.add(new DeckEnemiesDTO(value, numberOfStrengthEnemies, numberOfPowerEnemies ,numberOfStrengthOrPowerEnemies));
        } );

        return enemies;
    }

    private Map<FightType, Integer> returnMapFromString(String value, List<Card> cards) {

        return cards.stream().filter(card -> card.getFight_power().equals(value)).collect(groupingBy(Card::getFight_statistic, summingInt(Card::getNumber_of_copies)));
    }

    private int numberOfEnemiesFromType(List<Card> cards, FightType type) {
        Map<FightType, Integer> map = cards.stream().collect(groupingBy(Card::getFight_statistic, summingInt(Card::getNumber_of_copies)));

        return map.containsKey(FightType.SIŁA_MOC) ? map.get(type) + map.get(FightType.SIŁA_MOC) : map.get(type);
    }

    private double averageValueFromType(List<Card> cards, FightType type) {
        Map<FightType, Integer> numberOfCards = cards.stream().filter(card -> !card.getFight_power().equals("?"))
                .collect(groupingBy(Card::getFight_statistic, summingInt(Card::getNumber_of_copies)));

        Map<FightType, Integer> totalPower = cards.stream().filter(card -> !card.getFight_power().equals("?"))
                .collect(groupingBy(Card::getFight_statistic, summingInt(card -> card.getNumber_of_copies() * Integer.parseInt(card.getFight_power()))));

        if(numberOfCards.containsKey(FightType.SIŁA_MOC)) {
            return (totalPower.get(type) + totalPower.get(FightType.SIŁA_MOC))  / (double) (numberOfCards.get(type) + numberOfCards.get(FightType.SIŁA_MOC));
        }
        return totalPower.get(type) / (double) numberOfCards.get(type);
    }

    private int maxValueFromType(List<Card> cards, FightType type) {
        Map<FightType, Optional<Integer>> map = cards.stream().filter(card -> !card.getFight_power().equals("?"))
                .collect(groupingBy(Card::getFight_statistic, mapping(card -> Integer.parseInt(card.getFight_power()), maxBy(comparingInt(Integer::valueOf)))));

        return map.containsKey(FightType.SIŁA_MOC) ? Math.max(map.get(type).get(), map.get(FightType.SIŁA_MOC).get()) : map.get(type).get();
    }

    private int minValueFromType(List<Card> cards, FightType type) {
        Map<FightType, Optional<Integer>> map = cards.stream().filter(card -> !card.getFight_power().equals("?"))
                .collect(groupingBy(Card::getFight_statistic, mapping(card -> Integer.parseInt(card.getFight_power()), minBy(comparingInt(Integer::valueOf)))));

        return map.containsKey(FightType.SIŁA_MOC) ? Math.min(map.get(type).get(), map.get(FightType.SIŁA_MOC).get()) : map.get(type).get();
    }

    private Mono<DeckStatisticsDTO> createStatsFromAllDecks(List<DeckStatisticsDTO> decks) {
        var initialStatistics = DeckStatisticsDTO.builder(0).build();

        var aggregatedStatistics = decks.stream()
                .reduce(initialStatistics, (accumulated, deckStatisticsDTO) -> {
                    accumulated.setNumberOfCards(accumulated.getNumberOfCards() + deckStatisticsDTO.getNumberOfCards());
                    accumulated.setNumberOfEvents(accumulated.getNumberOfEvents() + deckStatisticsDTO.getNumberOfEvents());
                    accumulated.setNumberOfStrangers(accumulated.getNumberOfStrangers() + deckStatisticsDTO.getNumberOfStrangers());
                    accumulated.setNumberOfPlaces(accumulated.getNumberOfPlaces() + deckStatisticsDTO.getNumberOfPlaces());
                    accumulated.setNumberOfFollowers(accumulated.getNumberOfFollowers() + deckStatisticsDTO.getNumberOfFollowers());
                    accumulated.setNumberOfItems(accumulated.getNumberOfItems() + deckStatisticsDTO.getNumberOfItems());
                    accumulated.setNumberOfEnemies(accumulated.getNumberOfEnemies() + deckStatisticsDTO.getNumberOfEnemies());

                    return accumulated;
                });
        return Mono.just(aggregatedStatistics);
    }

    private Mono<DeckStatisticsDTO> createDeckStatisticsFromCards(List<Card> cards, TypeOfStatistics type) {

        Map<CardType, Integer> map;

        if(type.equals(TypeOfStatistics.UNIQUE))
            map = cards.stream().collect(groupingBy(Card::getType, collectingAndThen(counting(), Long::intValue)));
        else
            map = cards.stream().collect(groupingBy(Card::getType, summingInt(Card::getNumber_of_copies)));

        int numberOfCards = map.values().stream().mapToInt(Integer::intValue).sum();

        return Mono.just(DeckStatisticsDTO.builder(numberOfCards)
                .numberOfEvents((map.getOrDefault(CardType.ZDARZENIE, 0) + map.getOrDefault(CardType.KSIĘŻYCOWE_ZDARZENIE, 0)))
                .numberOfStrangers(map.getOrDefault(CardType.NIEZNAJOMY, 0))
                .numberOfPlaces(map.getOrDefault(CardType.MIEJSCE, 0))
                .numberOfFollowers(map.getOrDefault(CardType.PRZYJACIEL, 0))
                .numberOfItems(map.getOrDefault(CardType.PRZEDMIOT, 0) + map.getOrDefault(CardType.MAGICZNY_PRZEDMIOT, 0))
                .numberOfEnemies(map.getOrDefault(CardType.WRÓG, 0))
                .build());
    }
}
