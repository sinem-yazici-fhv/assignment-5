package microservice.battleship.shipandguess.application;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import microservice.battleship.shipandguess.domain.BoardConfig;
import microservice.battleship.shipandguess.domain.Ship;
import microservice.battleship.shipandguess.dto.GameDTO;
import microservice.battleship.shipandguess.dto.PlayerDTO;
import microservice.battleship.shipandguess.dto.ShipDTO;
import microservice.battleship.shipandguess.messaging.ShipEventPublisher;
import microservice.battleship.shipandguess.persistence.ShipRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipService {
    private final ShipRepository shipRepository;
    private final RestTemplate restTemplate;
    private final ShipEventPublisher shipEventPublisher;

    public ShipService(ShipRepository shipRepository,
                     RestTemplate restTemplate,
                     ShipEventPublisher shipEventPublisher) {
        this.shipRepository = shipRepository;
        this.shipEventPublisher = shipEventPublisher;
        this.restTemplate = restTemplate;
    }

    public Ship placeShip(Long playerId, Long gameId, int row, int col, int size, boolean isHorizontal) {
        try {
            validateShipPlacement(row, col, size, isHorizontal);
        } catch (IllegalArgumentException e) {
            System.out.println("Spieler " + playerId + ": " + e.getMessage());
            throw e;
        }

        PlayerDTO playerDTO = getPlayerFromPlayerService(playerId);
        GameDTO gameDTO = getGameFromGameService(gameId);

        Ship ship = new Ship(row, col, size, isHorizontal, playerDTO.getId(), gameDTO.getId());

        ship.setShipEventPublisher(shipEventPublisher);

        Ship savedShip = shipRepository.save(ship);

        System.out.println("Spieler " + playerId + " hat in Spiel " + gameId + " ein Schiff gesetzt");

        return savedShip;
    }

    private void validateShipPlacement(int row, int col, int size, boolean isHorizontal) {
        if (row < 0 || row >= BoardConfig.BOARD_SIZE || col < 0 || col >= BoardConfig.BOARD_SIZE) {
            throw new IllegalArgumentException("Schiff darf nicht außerhalb des Spielfelds gesetzt sein");
        }

        if (isHorizontal && (col + size > BoardConfig.BOARD_SIZE)) {
            throw new IllegalArgumentException("Schiff darf nicht außerhalb des Spielfelds gesetzt sein (horizontal)");
        }

        if (!isHorizontal && (row + size > BoardConfig.BOARD_SIZE)) {
            throw new IllegalArgumentException("Schiff darf nicht außerhalb des Spielfelds gesetzt sein (vertikal)");
        }
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

    public List<ShipDTO> getShipsByGameAndPlayer(Long gameId, Long playerId) {
        List<Ship> ships = shipRepository.findByGameIdAndPlayerId(gameId, playerId);
        return ships.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ShipDTO convertToDTO(Ship ship) {
        ShipDTO shipDTO = new ShipDTO();
        shipDTO.setId(ship.getId());
        shipDTO.setRow(ship.getRow());
        shipDTO.setCol(ship.getCol());
        shipDTO.setSize(ship.getSize());
        shipDTO.setHorizontal(ship.isHorizontal());
        shipDTO.setSunk(ship.isSunk());
        shipDTO.setHits(ship.getHits());
        shipDTO.setPlayerId(ship.getPlayerId());
        shipDTO.setGameId(ship.getGameId());
        return shipDTO;
    }

    public PlayerDTO playerServiceFallback(Long playerId, Throwable throwable) {
        System.out.println("Player Service nicht verfügbar: " + throwable.getMessage());
        return new PlayerDTO();
    }

    public GameDTO gameServiceFallback(Long gameId, Throwable throwable) {
        System.out.println("Game Service nicht verfügbar: " + throwable.getMessage());
        return new GameDTO();
    }
}

