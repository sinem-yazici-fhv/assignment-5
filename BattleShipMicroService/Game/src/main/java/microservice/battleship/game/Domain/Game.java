package microservice.battleship.game.Domain;

import jakarta.persistence.*;
import microservice.battleship.game.messaging.GameEventPublisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<Long> playerIds = new ArrayList<>();

    private Long winnerId;
    private boolean gameOver = false;

    public Game() {}

    public void addPlayer(Long playerId) {
        if (this.playerIds.size() < 2) {
            this.playerIds.add(playerId);
        } else {
            throw new IllegalArgumentException("A game can only have 2 players");
        }
    }

    public Map<String, Object> checkGameStatus(Function<Long, Boolean> areAllShipsSunk, GameEventPublisher gameEventPublisher) {
        if (playerIds.size() < 2) {
            throw new RuntimeException("Game does not have enough players");
        }

        Long player1Id = playerIds.get(0);
        Long player2Id = playerIds.get(1);

        boolean player1AllShipsSunk = areAllShipsSunk.apply(player1Id);
        boolean player2AllShipsSunk = areAllShipsSunk.apply(player2Id);

        if (player1AllShipsSunk || player2AllShipsSunk) {
            if (!isGameOver()) {
                setGameOver(true);
                setWinnerId(player1AllShipsSunk ? player2Id : player1Id);
                gameEventPublisher.publishGameOver(id, getWinnerId());
            }
        }

        Map<String, Object> status = new HashMap<>();
        status.put("gameOver", isGameOver());
        status.put("winner", getWinnerId());
        return status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<Long> playerIds) {
        this.playerIds = playerIds;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}