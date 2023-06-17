package com.talismanresourceserver.service;

import com.talismanresourceserver.exception.CardNotFoundException;
import com.talismanresourceserver.exception.DeckNotFoundException;
import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.model.type.FightType;
import com.talismanresourceserver.repository.DeckRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class CardService {

    private final DeckRepository deckRepository;

    public Flux<Deck> getAllDecks() {
        return deckRepository.findAll();
    }

    public Mono<Deck> getDeckByNameOfDeck(String nameOfDeck) {
        return deckRepository.findDeckByNameOfDeck(nameOfDeck)
                        .switchIfEmpty(Mono.defer(() ->
                                Mono.error(new DeckNotFoundException(String.format("Deck: %s not found!", nameOfDeck)))));
    }

    public Mono<Card> getCardFromDeckByName(String nameOfDeck, String nameOfCard) {
        return deckRepository.findCardInDeck(nameOfDeck, nameOfCard)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new CardNotFoundException(String.format("Card: %s in deck: %s not found!", nameOfCard, nameOfDeck)))))
                .map(Deck::getCards).map(response -> response.get(0));
    }

    public Flux<Card> getCardsFromTypeInDeck(String nameOfDeck, CardType type) {
        return deckRepository.findCardsOfTypeInDeck(nameOfDeck, type)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new CardNotFoundException(String.format("Cards of type: %s in deck: %s not found!", type, nameOfDeck)))))
                .flatMapIterable(Deck::getCards);
    }

    public Flux<Card> getEnemiesFromDeckByStatisticType(String nameOfDeck, FightType fightStatistic) {
        return deckRepository.findEnemiesInDeckByStatistic(nameOfDeck, fightStatistic)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new CardNotFoundException(String.format("Enemies of type: %s in deck: %s not found!", fightStatistic, nameOfDeck)))))
                .flatMapIterable(Deck::getCards);
    }
}
