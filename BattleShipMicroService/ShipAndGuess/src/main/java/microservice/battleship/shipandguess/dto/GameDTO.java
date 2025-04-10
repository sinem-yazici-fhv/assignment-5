package microservice.battleship.shipandguess.dto;

import java.util.List;

public class GameDTO {
    private Long id;
    private List<Long> playerIds;
    private boolean gameOver;
    private Long winnerId;


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

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }
}