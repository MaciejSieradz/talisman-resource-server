package com.talismanresourceserver.integration.controller;

import com.talismanresourceserver.controller.DeckStatisticsController;
import com.talismanresourceserver.dto.DeckStatisticsDTO;
import com.talismanresourceserver.service.StatisticsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@WebFluxTest(controllers = DeckStatisticsController.class)
public class DeckStatisticControllerWebOnlyTests {

    @MockBean
    private StatisticsService statisticsService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithMockUser
    void shouldReturnDeckStatisticsDTO() {

        DeckStatisticsDTO statistics = DeckStatisticsDTO.builder(3).numberOfEnemies(2).numberOfEvents(1).build();

        given(statisticsService.getBasicStatisticsFromAllCardsOfDeck(anyString())).willReturn(Mono.just(statistics));

        webTestClient.get()
                .uri("/api/statistics/all/deck")
                .exchange()
                .expectStatus().isOk()
                .expectBody(DeckStatisticsDTO.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();

                    Assertions.assertThat(body).isNotNull();
                    Assertions.assertThat(body).isEqualTo(statistics);
                });
    }

}
