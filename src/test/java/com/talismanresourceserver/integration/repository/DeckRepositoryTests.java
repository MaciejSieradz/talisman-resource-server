package com.talismanresourceserver.integration.repository;

import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.model.type.FightType;
import com.talismanresourceserver.repository.DeckRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@DataMongoTest
@Testcontainers
public class DeckRepositoryTests {

    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private DeckRepository deckRepository;

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
    void shouldGetRepositories() {
        Assertions.assertThat(deckRepository.findAll().toIterable()).hasSize(2);
        Assertions.assertThat(deckRepository.findAll().blockFirst()).isEqualTo(decks.get(0));
    }

    @Test
    void shouldReturnEmpty() {
        Assertions.assertThat(deckRepository.findDeckByNameOfDeck("invalid-name").block()).isNull();
    }

    @Test
    void shouldReturnDeckByname() {
        Assertions.assertThat(deckRepository.findDeckByNameOfDeck("test-name-one").block()).isEqualTo(decks.get(0));
    }

    @Test
    void shouldReturnCardsOfTypeInDeck() {
        var cards = deckRepository.findCardsOfTypeInDeck("test-name-one", CardType.MIEJSCE).block().getCards();

        Assertions.assertThat(cards).isNotNull();
        Assertions.assertThat(cards.size()).isEqualTo(1);
        Assertions.assertThat(cards.get(0)).isEqualTo(placeCard);
    }

    @Test
    void shouldReturnEnemiesFromDeckByStatistic() {
        var enemiesWithStrength = deckRepository.findEnemiesInDeckByStatistic("test-name-one", FightType.SIŁA).block().getCards();

        Assertions.assertThat(enemiesWithStrength).isNotNull();
        Assertions.assertThat(enemiesWithStrength.size()).isEqualTo(1);
        Assertions.assertThat(enemiesWithStrength.get(0)).isEqualTo(enemyCard);
    }
}
