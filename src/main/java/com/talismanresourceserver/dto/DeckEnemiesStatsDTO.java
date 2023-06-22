package com.talismanresourceserver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeckEnemiesStatsDTO {
    private int numberOfEnemies;
    private int numberOfEnemiesWithStrength;
    private double averageEnemyStrength;
    private int minEnemyStrength;
    private int maxEnemyStrength;
    private int numberOfEnemiesWithPower;
    private double averageEnemyPower;
    private int minEnemyPower;
    private int maxEnemyPower;
}
