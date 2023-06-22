package com.talismanresourceserver.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public final class DeckStatisticsDTO {

    private int numberOfCards;

    private int numberOfEvents;
    private int numberOfStrangers;
    private int numberOfPlaces;
    private int numberOfFollowers;
    private int numberOfItems;
    private int numberOfEnemies;

    private static DeckStatisticsDTOBuilder builder() {
        return new DeckStatisticsDTOBuilder();
    }

    public static DeckStatisticsDTOBuilder builder(int numberOfCards) {
        return builder().numberOfCards(numberOfCards);
    }
}
