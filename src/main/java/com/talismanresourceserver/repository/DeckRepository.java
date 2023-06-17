package com.talismanresourceserver.repository;

import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.model.type.FightType;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface DeckRepository extends ReactiveMongoRepository<Deck, String> {

    Mono<Deck> findDeckByNameOfDeck(String nameOfDeck);

    @Query(
            value = "{$and: [{'name_of_deck': ?0},{'cards.name': {$in: [?1]}}]}",
            fields = "{'cards.$': 1}")
    Mono<Deck> findCardInDeck(String deck, String name);

    @Aggregation(
            pipeline = {
                    "{$match : {name_of_deck:?0}}",
                    "{$project : {cards : {$filter : {input:'$cards', as : 'cards', cond :"
                            + " {$eq:['$$cards.type', ?1]}}}, name_of_deck: 1 }}"
            })
    Mono<Deck> findCardsOfTypeInDeck(String deck, CardType type);

    @Aggregation(
            pipeline = {
                    "{$match: {name_of_deck:?0}}",
                    "{$project : {cards : {$filter : {input:'$cards', as : 'cards', cond :"
                            + " {$eq:['$$cards.type', 'Wróg'], $eq:['$$cards.fight_statistic', ?1]}}},"
                            + " name_of_deck: 1 }}"
            })
    Mono<Deck> findEnemiesInDeckByStatistic(String deck, FightType fight_statistic);
}
