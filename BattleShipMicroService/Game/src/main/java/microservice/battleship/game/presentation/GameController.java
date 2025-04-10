package microservice.battleship.game.presentation;

import microservice.battleship.game.dto.PlayerDTO;
import org.springframework.web.bind.annotation.*;
import microservice.battleship.game.Domain.Game;
import microservice.battleship.game.application.GameService;


import java.util.Map;
import java.util.Optional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final RestTemplate restTemplate;

    public GameController(GameService gameService, RestTemplate restTemplate) {
        this.gameService = gameService;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public Game createGame() {
        return gameService.createGame();
    }

    @GetMapping("/{id}")
    public Optional<Game> getGame(@PathVariable Long id) {
        return gameService.getGame(id);

    }

    @GetMapping("/{gameId}/status")
    public Map<String, Object> getGameStatus(@PathVariable Long gameId) {
        return gameService.getGameStatus(gameId);
    }

    @PostMapping("/{gameId}/addPlayer/{playerId}")
    public void addPlayerToGame(@PathVariable Long gameId, @PathVariable Long playerId) {
        gameService.addPlayerToGame(gameId, playerId);
    }
}