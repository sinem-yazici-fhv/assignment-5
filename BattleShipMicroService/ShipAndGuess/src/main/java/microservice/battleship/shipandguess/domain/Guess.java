package microservice.battleship.shipandguess.domain;
import jakarta.persistence.*;

@Entity
public class Guess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rowIndex;
    private int col;
    private boolean hit;
    private Long shipId;
    private Long playerId;
    private Long gameId;

    public Guess() {}

    public Guess(int rowIndex, int col, boolean hit, Long playerId, Long gameId) {
        this.rowIndex = rowIndex;
        this.col = col;
        this.hit = hit;
        this.playerId = playerId;
        this.gameId = gameId;
    }

      public Long getId() {
        return id;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getCol() {
        return col;
    }

    public boolean isHit() {
        return hit;
    }

    public Long getShipId() {
        return shipId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public Long getGameId() {
        return gameId;
    }
}



