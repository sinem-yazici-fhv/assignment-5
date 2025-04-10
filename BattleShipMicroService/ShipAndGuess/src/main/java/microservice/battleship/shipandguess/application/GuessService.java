package microservice.battleship.shipandguess.application;

import microservice.battleship.shipandguess.domain.BoardConfig;
import microservice.battleship.shipandguess.domain.Guess;
import microservice.battleship.shipandguess.domain.Ship;
import microservice.battleship.shipandguess.dto.GameDTO;
import microservice.battleship.shipandguess.dto.PlayerDTO;
import microservice.battleship.shipandguess.messaging.ShipEventPublisher;
import microservice.battleship.shipandguess.persistence.GuessRepository;
import microservice.battleship.shipandguess.persistence.ShipRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GuessService {
    private final GuessRepository guessRepository;
    private final ShipRepository shipRepository;
    private final RestTemplate restTemplate;
    private final ShipEventPublisher shipEventPublisher;

    public GuessService(GuessRepository guessRepository,
                      ShipRepository shipRepository,
                      RestTemplate restTemplate,
                      ShipEventPublisher shipEventPublisher) {
        this.guessRepository = guessRepository;
        this.shipRepository = shipRepository;
        this.restTemplate = restTemplate;
        this.shipEventPublisher = shipEventPublisher;
    }

    public Map<String, Object> makeGuess(Long playerId, Long gameId, int rowIndex, int col) {
        // Prüfen, ob der Schuss innerhalb des Spielfelds liegt
        if (rowIndex < 0 || rowIndex >= BoardConfig.BOARD_SIZE || col < 0 || col >= BoardConfig.BOARD_SIZE) {
            System.out.println("Spieler " + playerId + ": Schuss darf nicht außerhalb des Spielfelds sein");
            throw new IllegalArgumentException("Schuss darf nicht außerhalb des Spielfelds sein");
        }

        PlayerDTO playerDTO = getPlayerFromPlayerService(playerId);
        GameDTO gameDTO = getGameFromGameService(gameId);

        Long opponentId = gameDTO.getPlayerIds().stream()
                .filter(id -> !id.equals(playerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Gegner nicht gefunden"));

        boolean hit = false;
        Long hitShipId = null;
        List<Ship> opponentShips = shipRepository.findByGameIdAndPlayerId(gameId, opponentId);
        for (Ship ship : opponentShips) {
            if (ship.isHit(rowIndex, col)) {
                hit = true;
                hitShipId = ship.getId();

                // Get the ship from repository to ensure we have the latest state
                Ship hitShip = shipRepository.findById(hitShipId).orElseThrow();

                // Set the publisher before marking the hit
                hitShip.setShipEventPublisher(shipEventPublisher);

                // Ausgabe für Treffer
                System.out.println("Spieler " + playerId + " hat ein Schiff von Gegner " + opponentId + " getroffen");

                // Mark the hit which may trigger a ship sunk event
                hitShip.markHit();

                // Prüfen ob alle Schiffe versenkt sind
                boolean allSunk = true;
                for (Ship s : opponentShips) {
                    if (!s.isSunk() && !s.getId().equals(hitShipId)) {
                        allSunk = false;
                        break;
                    }
                }

                if (hitShip.isSunk() && allSunk) {
                    System.out.println("Alle Schiffe von Gegner " + opponentId + " wurden versenkt");
                }

                shipRepository.save(hitShip);
                break;
            }
        }

        // Ausgabe für Nicht-Treffer
        if (!hit) {
            System.out.println("Spieler " + playerId + " hat kein Schiff des Gegners " + opponentId + " getroffen. Gegner ist dran.");
        }

        // Guess speichern
        Guess guess = new Guess(rowIndex, col, hit, playerDTO.getId(), gameDTO.getId());
        if (hit) {
            guess.setShipId(hitShipId);
        }
        guessRepository.save(guess);

        Map<String, Object> gameStatus = getGameStatus(gameId);

        Map<String, Object> response = new HashMap<>();
        response.put("result", hit ? "hit" : "miss");
        response.put("shipId", hit ? hitShipId : null);
        response.put("gameOver", gameStatus.get("gameOver"));
        response.put("winner", gameStatus.get("winner"));

        return response;
    }

    @CircuitBreaker(name = "playerServiceCircuitBreaker", fallbackMethod = "playerServiceFallback")
    private PlayerDTO getPlayerFromPlayerService(Long playerId) {
        String playerServiceUrl = "http://player-service/players/" + playerId;
        return restTemplate.getForObject(playerServiceUrl, PlayerDTO.class);
    }

    @CircuitBreaker(name = "gameServiceCircuitBreaker", fallbackMethod = "gameServiceFallback")
    private GameDTO getGameFromGameService(Long gameId) {
        String gameServiceUrl = "http://game-service/game/" + gameId;
        return restTemplate.getForObject(gameServiceUrl, GameDTO.class);
    }

    @CircuitBreaker(name = "gameServiceCircuitBreaker", fallbackMethod = "gameStatusFallback")
    private Map<String, Object> getGameStatus(Long gameId) {
        String gameServiceUrl = "http://game-service/game/" + gameId + "/status";
        return restTemplate.getForObject(gameServiceUrl, Map.class);
    }

    private PlayerDTO playerServiceFallback(Long playerId, Throwable throwable) {
        System.out.println("Player Service nicht verfügbar: " + throwable.getMessage());
        return new PlayerDTO();
    }

    private GameDTO gameServiceFallback(Long gameId, Throwable throwable) {
        System.out.println("Game Service nicht verfügbar: " + throwable.getMessage());
        return new GameDTO();
    }

    private Map<String, Object> gameStatusFallback(Long gameId, Throwable throwable) {
        System.out.println("Game Status nicht verfügbar: " + throwable.getMessage());
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("gameOver", false);
        fallback.put("winner", null);
        return fallback;
    }
}

