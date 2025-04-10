package microservice.battleship.player.application;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import microservice.battleship.player.domain.Player;
import microservice.battleship.player.dto.GameDTO;
import microservice.battleship.player.persistence.PlayerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.Optional;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final RestTemplate restTemplate;

    public PlayerService(PlayerRepository playerRepository, RestTemplate restTemplate) {
        this.playerRepository = playerRepository;
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "gameServiceCircuitBreaker", fallbackMethod = "gameServiceFallback")
    public Player createPlayer(String name, Long gameId) {
        String gameServiceUrl = "http://game-service/game/" + gameId;
        GameDTO gameDTO = restTemplate.getForObject(gameServiceUrl, GameDTO.class);

        if (gameDTO == null) {
            throw new RuntimeException("Spiel nicht gefunden: " + gameId);
        }

        Player player = new Player(name);
        player.setGameId(gameId);
        Player savedPlayer = playerRepository.save(player);

        System.out.println("Spieler " + savedPlayer.getId() + " mit dem Namen '" + savedPlayer.getName() + "' erstellt");

        return savedPlayer;
    }

    public Player gameServiceFallback(String name, Long gameId, Exception ex) {
        System.out.println("Game Service nicht verfügbar: " + ex.getMessage());
        Player fallbackPlayer = new Player("Fallback Spieler");
        fallbackPlayer.setGameId(-1L); // Spiel-ID für Fallback
        return fallbackPlayer;
    }

    public Optional<Player> getPlayer(Long id) {
        return playerRepository.findById(id);
    }
}

