package com.talismanresourceserver.service;

import com.talismanresourceserver.dto.DeckStatisticsDTO;
import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.repository.DeckRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final DeckRepository deckRepository;

    private enum TypeOfStatistics {UNIQUE, ALL}

    public Mono<DeckStatisticsDTO> getBasicStatisticsFromUniqueCards() {
        return deckRepository.findAll().flatMap(deck -> getBasicStatisticsFromUniqueCardsOfDeck(deck.getNameOfDeck()))
                .collectList().flatMap(this::createStatisticsFromAllDecks);
    }

    public Mono<DeckStatisticsDTO> getBasicStatisticsFromAllCards() {
        return deckRepository.findAll().flatMap(deck -> getBasicStatisticsFromAllCardsOfDeck(deck.getNameOfDeck()))
                .collectList().flatMap(this::createStatisticsFromAllDecks);
    }

    public Mono<DeckStatisticsDTO> getBasicStatisticsFromUniqueCardsOfDeck(String nameOfDeck) {
        return deckRepository.findDeckByNameOfDeck(nameOfDeck).map(Deck::getCards)
                .flatMap(cards -> createDeckStatisticsFromCards(cards, TypeOfStatistics.UNIQUE));
    }

    public Mono<DeckStatisticsDTO> getBasicStatisticsFromAllCardsOfDeck(String nameOfDeck) {
        return deckRepository.findDeckByNameOfDeck(nameOfDeck).map(Deck::getCards)
                .flatMap(cards -> createDeckStatisticsFromCards(cards, TypeOfStatistics.ALL));
    }

    private Mono<DeckStatisticsDTO> createStatisticsFromAllDecks(List<DeckStatisticsDTO> decks) {
        DeckStatisticsDTO initialStatistics = DeckStatisticsDTO.builder(0).build();

        DeckStatisticsDTO aggregatedStatistics = decks.stream()
                .reduce(initialStatistics, (accumulated, deckStatisticsDTO) -> {
                    int numberOfCards = accumulated.getNumberOfCards() + deckStatisticsDTO.getNumberOfCards();
                    int numberOfEvents = accumulated.getNumberOfEvents() + deckStatisticsDTO.getNumberOfEvents();
                    int numberOfStrangers = accumulated.getNumberOfStrangers() + deckStatisticsDTO.getNumberOfStrangers();
                    int numberOfPlaces = accumulated.getNumberOfPlaces() + deckStatisticsDTO.getNumberOfPlaces();
                    int numberOfFollowers = accumulated.getNumberOfFollowers() + deckStatisticsDTO.getNumberOfFollowers();
                    int numberOfItems = accumulated.getNumberOfItems() + deckStatisticsDTO.getNumberOfItems();
                    int numberOfEnemies = accumulated.getNumberOfEnemies() + deckStatisticsDTO.getNumberOfEnemies();

                    return DeckStatisticsDTO.builder(numberOfCards)
                            .numberOfEvents(numberOfEvents)
                            .numberOfStrangers(numberOfStrangers)
                            .numberOfPlaces(numberOfPlaces)
                            .numberOfFollowers(numberOfFollowers)
                            .numberOfItems(numberOfItems)
                            .numberOfEnemies(numberOfEnemies)
                            .build();
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
