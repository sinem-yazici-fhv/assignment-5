package microservice.battleship.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game-details")
public class GameDetailsController {

    private final WebClient.Builder webClientBuilder;
    private final ReactiveCircuitBreakerFactory circuitBreakerFactory;

    public GameDetailsController(WebClient.Builder webClientBuilder,
                                ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        this.webClientBuilder = webClientBuilder;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @GetMapping("/{gameId}")
    public Mono<Map<String, Object>> getGameDetails(@PathVariable Long gameId) {
        ReactiveCircuitBreaker gameCircuitBreaker =
            circuitBreakerFactory.create("gameServiceCircuitBreaker");
        ReactiveCircuitBreaker playerCircuitBreaker =
            circuitBreakerFactory.create("playerServiceCircuitBreaker");
        ReactiveCircuitBreaker shipCircuitBreaker =
            circuitBreakerFactory.create("shipAndGuessServiceCircuitBreaker");

        Mono<Map> gameMono = gameCircuitBreaker.run(
            webClientBuilder.build()
                .get()
                .uri("http://game-service/game/{id}", gameId)
                .retrieve()
                .bodyToMono(Map.class),
            throwable -> fallbackGameInfo(gameId)
        );

        Mono<Map> statusMono = gameCircuitBreaker.run(
            webClientBuilder.build()
                .get()
                .uri("http://game-service/game/{gameId}/status", gameId)
                .retrieve()
                .bodyToMono(Map.class),
            throwable -> fallbackGameStatus()
        );

        return gameMono.flatMap(gameInfo -> {
            List<Integer> playerIds = (List<Integer>) gameInfo.get("playerIds");

            if (playerIds == null || playerIds.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("game", gameInfo);
                result.put("status", Mono.just(fallbackGameStatus()).block());
                result.put("players", List.of());
                result.put("ships", Map.of());
                return Mono.just(result);
            }

            Mono<List<Map>> playersMono = Mono.just(playerIds)
                .flatMapMany(ids -> Mono.just(ids).flatMapIterable(id -> id))
                .flatMap(playerId -> playerCircuitBreaker.run(
                    webClientBuilder.build()
                        .get()
                        .uri("http://player-service/players/{id}", playerId)
                        .retrieve()
                        .bodyToMono(Map.class),
                    throwable -> fallbackPlayerInfo(Long.valueOf(playerId.toString()))
                ))
                .collectList();

            Mono<Map<String, List<Map>>> shipsMono = Mono.just(playerIds)
                .flatMapMany(ids -> Mono.just(ids).flatMapIterable(id -> id))
                .flatMap(playerId -> shipCircuitBreaker.run(
                    webClientBuilder.build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("ship-and-guess-service")
                            .path("/ships")
                            .queryParam("gameId", gameId)
                            .queryParam("playerId", playerId)
                            .build())
                        .retrieve()
                        .bodyToMono(List.class)
                        .map(ships -> Map.entry(playerId.toString(), ships)),
                    throwable -> Mono.just(Map.entry(playerId.toString(), List.of()))
                ))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);

            return Mono.zip(statusMono, playersMono, shipsMono)
                .map(tuple -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("game", gameInfo);
                    result.put("status", tuple.getT1());
                    result.put("players", tuple.getT2());
                    result.put("ships", tuple.getT3());
                    return result;
                });
        });
    }

    private Mono<Map> fallbackGameInfo(Long gameId) {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("id", gameId);
        fallback.put("playerIds", List.of());
        fallback.put("error", "Game information temporarily unavailable");
        return Mono.just(fallback);
    }

    private Mono<Map> fallbackGameStatus() {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("gameOver", false);
        fallback.put("winner", null);
        fallback.put("error", "Game status temporarily unavailable");
        return Mono.just(fallback);
    }

    private Mono<Map> fallbackPlayerInfo(Long playerId) {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("id", playerId);
        fallback.put("name", "Unknown Player");
        fallback.put("error", "Player information temporarily unavailable");
        return Mono.just(fallback);
    }
}