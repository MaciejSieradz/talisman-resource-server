package com.talismanresourceserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "cards")
@Data
@AllArgsConstructor
@Builder
public class Deck {

    @Id
    private String id;

    @Field(name = "name_of_deck")
    private String nameOfDeck;

    private List<Card> cards;
}
