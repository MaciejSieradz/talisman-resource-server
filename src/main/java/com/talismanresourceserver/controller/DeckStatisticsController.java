package com.talismanresourceserver.controller;

import com.talismanresourceserver.dto.DeckEnemiesDTO;
import com.talismanresourceserver.dto.DeckEnemiesStatsDTO;
import com.talismanresourceserver.dto.DeckStatisticsDTO;
import com.talismanresourceserver.service.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
@AllArgsConstructor
public class DeckStatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/unique")
    public Mono<DeckStatisticsDTO> getUniqueStasts() {
        return statisticsService.getUniqueStats();
    }

    @GetMapping("/unique/{nameOfDeck}")
    public Mono<DeckStatisticsDTO> getUniqueStatsFromDeck(@PathVariable String nameOfDeck) {
        return statisticsService.getUniqueStatsFromDeck(nameOfDeck);
    }

    @GetMapping("/all/{nameOfDeck}")
    public Mono<DeckStatisticsDTO> getAllStatsFromDeck(@PathVariable String nameOfDeck) {
        return statisticsService.getAllStatsFromDeck(nameOfDeck);
    }

    @GetMapping("/all")
    public Mono<DeckStatisticsDTO> getAllStats() {
        return statisticsService.getAllStats();
    }

    @GetMapping("/all/{nameOfDeck}/enemies")
    public Mono<DeckEnemiesStatsDTO> getAllEnemiesStats(@PathVariable String nameOfDeck) {
        return statisticsService.getAllEnemiesFromDeck(nameOfDeck);
    }

    @GetMapping("/enemies/{deck}")
    public ResponseEntity<Flux<DeckEnemiesDTO>> getAllEnemies(@PathVariable("deck") String nameOfDeck) {
        return ResponseEntity.ok(statisticsService.getNumberOfEnemies(nameOfDeck));
    }
}
