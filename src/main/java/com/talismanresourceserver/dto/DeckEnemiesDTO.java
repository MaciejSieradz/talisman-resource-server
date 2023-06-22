package com.talismanresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeckEnemiesDTO {

    private String fightPower;
    private int numberOfStrengthEnemies;
    private int numberOfPowerEnemies;
    private int numberOfStrengthOrPowerEnemies;

}
