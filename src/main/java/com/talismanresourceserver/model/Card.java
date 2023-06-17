package com.talismanresourceserver.model;

import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.model.type.FightType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {

    private String name;

    private CardType type;
    private String subtype;

    private FightType fight_statistic;
    private String fight_power;

    private int meeting_number;
    private int number_of_copies;

    private String description;
}
