package com.talismanresourceserver.controller;

import com.talismanresourceserver.dto.DeckStatisticsDTO;
import com.talismanresourceserver.service.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
@AllArgsConstructor
public class DeckStatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/unique")
    public Mono<DeckStatisticsDTO> getBasicStatisticsFromUniqueCards() {
        return statisticsService.getBasicStatisticsFromUniqueCards();
    }

    @GetMapping("/unique/{nameOfDeck}")
    public Mono<DeckStatisticsDTO> getBasicStatisticsFromUniqueCardsFromDeck(@PathVariable String nameOfDeck) {
        return statisticsService.getBasicStatisticsFromUniqueCardsOfDeck(nameOfDeck);
    }

    @GetMapping("/all/{nameOfDeck}")
    public Mono<DeckStatisticsDTO> getBasicStatisticsFromAllCardsFromDeck(@PathVariable String nameOfDeck) {
        return statisticsService.getBasicStatisticsFromAllCardsOfDeck(nameOfDeck);
    }

    @GetMapping("/all")
    public Mono<DeckStatisticsDTO> getBasicStatisticsFromAllCards() {
        return statisticsService.getBasicStatisticsFromAllCards();
    }
}
