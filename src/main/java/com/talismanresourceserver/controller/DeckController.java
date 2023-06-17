package com.talismanresourceserver.controller;

import com.talismanresourceserver.dto.ExceptionResponseDTO;
import com.talismanresourceserver.exception.CardNotFoundException;
import com.talismanresourceserver.exception.DeckNotFoundException;
import com.talismanresourceserver.model.Card;
import com.talismanresourceserver.model.Deck;
import com.talismanresourceserver.model.type.CardType;
import com.talismanresourceserver.model.type.FightType;
import com.talismanresourceserver.service.CardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@CrossOrigin
@AllArgsConstructor
@Slf4j
public class DeckController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Flux<Deck>> getDecks() {
        return ResponseEntity.ok(cardService.getAllDecks());
    }

    @GetMapping("/{nameOfDeck}")
    public ResponseEntity<Mono<Deck>> getDeckByName(@PathVariable String nameOfDeck) {
        return ResponseEntity.ok(cardService.getDeckByNameOfDeck(nameOfDeck));
    }

    @GetMapping("/{nameOfDeck}/{nameOfCard}")
    public ResponseEntity<Mono<Card>> getCardFromDeck(@PathVariable("nameOfDeck") String nameOfDeck, @PathVariable("nameOfCard") String nameOfCard) {
        return ResponseEntity.ok(cardService.getCardFromDeckByName(nameOfDeck, nameOfCard));
    }

    @GetMapping(value = "/{nameOfDeck}", params = "type")
    public ResponseEntity<Flux<Card>> getCardsOfTypeFromDeck(@PathVariable String nameOfDeck, @RequestParam("type") CardType type) {
        return ResponseEntity.ok(cardService.getCardsFromTypeInDeck(nameOfDeck, type));
    }

    @GetMapping("/{deck}/enemies/{fight_statistic}")
    public ResponseEntity<Flux<Card>> getEnemiesFromDeckByFightStatistic(
            @PathVariable(name = "deck") String nameOfDeck,
            @PathVariable(name = "fight_statistic") FightType fightStatistic) {
        return ResponseEntity.ok(cardService.getEnemiesFromDeckByStatisticType(nameOfDeck, fightStatistic));
    }

    @GetMapping("/api/card")
    public ResponseEntity<Mono<String>> getCard() {
        return ResponseEntity.ok(Mono.just("Hello!"));
    }

    @ExceptionHandler(DeckNotFoundException.class)
    public ResponseEntity<Mono<ExceptionResponseDTO>> deckNotFoundExceptionResponse(DeckNotFoundException deckNotFoundException) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Mono.just(new ExceptionResponseDTO(HttpStatus.NOT_FOUND.value(), deckNotFoundException.getMessage())));
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Mono<ExceptionResponseDTO>> cardNotFoundExceptionResponse(CardNotFoundException cardNotFoundException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Mono.just(new ExceptionResponseDTO(HttpStatus.NOT_FOUND.value(), cardNotFoundException.getMessage())));
    }
}
