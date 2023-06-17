package com.talismanresourceserver.service;

import com.talismanresourceserver.dto.DeckStatisticsDTO;
import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.repository.DeckRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final DeckRepository deckRepository;

    public Mono<DeckStatisticsDTO> getBasicStatisticsFromUniqueCardsFromAllDecks() {
        return deckRepository.findAll().flatMap(deck -> getBasicStatisticsFromUniqueCardsOfDeck(deck.getNameOfDeck()))
                .collectList().flatMap(deckStatisticsDTOS -> {
                    int numberOfCards = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfCards).sum();

                    int numberOfEvents = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfEvents).sum();
                    int numberOfStrangers = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfStrangers).sum();
                    int numberOfPlaces = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfPlaces).sum();
                    int numberOfFollowers = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfFollowers).sum();
                    int numberOfItems = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfItems).sum();
                    int numberOfEnemies = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfEnemies).sum();

                    return Mono.just(DeckStatisticsDTO.builder(numberOfCards)
                            .numberOfEvents(numberOfEvents)
                            .numberOfStrangers(numberOfStrangers)
                            .numberOfPlaces(numberOfPlaces)
                            .numberOfFollowers(numberOfFollowers)
                            .numberOfItems(numberOfItems)
                            .numberOfEnemies(numberOfEnemies)
                            .build());
                });
    }

    public Mono<DeckStatisticsDTO> getBasicStatisticsFromAllCards() {
        return deckRepository.findAll().flatMap(deck -> getBasicStatisticsFromAllCardsOfDeck(deck.getNameOfDeck()))
                .collectList().flatMap(deckStatisticsDTOS -> {
                    int numberOfCards = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfCards).sum();

                    int numberOfEvents = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfEvents).sum();
                    int numberOfStrangers = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfStrangers).sum();
                    int numberOfPlaces = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfPlaces).sum();
                    int numberOfFollowers = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfFollowers).sum();
                    int numberOfItems = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfItems).sum();
                    int numberOfEnemies = deckStatisticsDTOS.stream().mapToInt(DeckStatisticsDTO::getNumberOfEnemies).sum();

                    return Mono.just(DeckStatisticsDTO.builder(numberOfCards)
                            .numberOfEvents(numberOfEvents)
                            .numberOfStrangers(numberOfStrangers)
                            .numberOfPlaces(numberOfPlaces)
                            .numberOfFollowers(numberOfFollowers)
                            .numberOfItems(numberOfItems)
                            .numberOfEnemies(numberOfEnemies)
                            .build());
                });
    }

    public Mono<DeckStatisticsDTO> getBasicStatisticsFromUniqueCardsOfDeck(String nameOfDeck) {
        return deckRepository.findDeckByNameOfDeck(nameOfDeck).map(Deck::getCards)
                .flatMap(cards -> {
                   int numberOfCards = cards.size();

                   int numberOfEvents = (int) cards.stream().filter(card -> card.getType().equals(CardType.ZDARZENIE)
                   || card.getType().equals(CardType.KSIĘŻYCOWE_ZDARZENIE)).count();
                   int numberOfStrangers = (int) cards.stream().filter(card -> card.getType().equals(CardType.NIEZNAJOMY)).count();
                   int numberOfPlaces = (int) cards.stream().filter(card -> card.getType().equals(CardType.MIEJSCE)).count();
                   int numberOfFollowers = (int) cards.stream().filter(card -> card.getType().equals(CardType.PRZYJACIEL)).count();
                   int numberOfItems = (int) cards.stream().filter(card -> card.getType().equals(CardType.PRZEDMIOT)
                   || card.getType().equals(CardType.MAGICZNY_PRZEDMIOT)).count();
                   int numberOfEnemies = (int) cards.stream().filter(card -> card.getType().equals(CardType.WRÓG)).count();

                   return Mono.just(DeckStatisticsDTO.builder(numberOfCards)
                           .numberOfEvents(numberOfEvents)
                           .numberOfStrangers(numberOfStrangers)
                           .numberOfPlaces(numberOfPlaces)
                           .numberOfFollowers(numberOfFollowers)
                           .numberOfItems(numberOfItems)
                           .numberOfEnemies(numberOfEnemies)
                           .build());
                });
    }

    public Mono<DeckStatisticsDTO> getBasicStatisticsFromAllCardsOfDeck(String nameOfDeck) {
        return deckRepository.findDeckByNameOfDeck(nameOfDeck).map(Deck::getCards)
                .flatMap(cards -> {

                    int numberOfEvents = cards.stream().filter(card -> card.getType().equals(CardType.ZDARZENIE)
                            || card.getType().equals(CardType.KSIĘŻYCOWE_ZDARZENIE)).mapToInt(Card::getNumber_of_copies).sum();
                    int numberOfStrangers = cards.stream().filter(card -> card.getType().equals(CardType.NIEZNAJOMY)).mapToInt(Card::getNumber_of_copies).sum();
                    int numberOfPlaces = cards.stream().filter(card -> card.getType().equals(CardType.MIEJSCE)).mapToInt(Card::getNumber_of_copies).sum();
                    int numberOfFollowers = cards.stream().filter(card -> card.getType().equals(CardType.PRZYJACIEL)).mapToInt(Card::getNumber_of_copies).sum();
                    int numberOfItems = cards.stream().filter(card -> card.getType().equals(CardType.PRZEDMIOT)
                            || card.getType().equals(CardType.MAGICZNY_PRZEDMIOT)).mapToInt(Card::getNumber_of_copies).sum();
                    int numberOfEnemies = cards.stream().filter(card -> card.getType().equals(CardType.WRÓG)).mapToInt(Card::getNumber_of_copies).sum();

                    int numberOfCards = numberOfEvents + numberOfStrangers + numberOfPlaces + numberOfFollowers + numberOfItems + numberOfEnemies;

                    return Mono.just(DeckStatisticsDTO.builder(numberOfCards)
                            .numberOfEvents(numberOfEvents)
                            .numberOfStrangers(numberOfStrangers)
                            .numberOfPlaces(numberOfPlaces)
                            .numberOfFollowers(numberOfFollowers)
                            .numberOfItems(numberOfItems)
                            .numberOfEnemies(numberOfEnemies)
                            .build());
                });
    }
}
