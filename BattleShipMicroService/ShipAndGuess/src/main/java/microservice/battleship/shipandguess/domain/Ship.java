package microservice.battleship.shipandguess.domain;
import jakarta.persistence.*;
import microservice.battleship.shipandguess.messaging.ShipEventPublisher;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ship_row")
    private int row;

    private int col;
    private int size;
    private boolean isHorizontal;
    private boolean sunk = false;
    private int hits = 0;

    private Long playerId;
    private Long gameId;

    @Transient
    private ShipEventPublisher shipEventPublisher;

    public Ship() {
    }

    public Ship(int row, int col, int size, boolean isHorizontal, Long playerId, Long gameId) {
        if (row < 0 || row >= BoardConfig.BOARD_SIZE || col < 0 || col >= BoardConfig.BOARD_SIZE) {
            throw new IllegalArgumentException("Schiff kann nicht außerhalb des Boards platziert werden.");
        }
        if (isHorizontal && (col + size > BoardConfig.BOARD_SIZE)) {
            throw new IllegalArgumentException("Schiff passt nicht ins Board (horizontal).");
        }
        if (!isHorizontal && (row + size > BoardConfig.BOARD_SIZE)) {
            throw new IllegalArgumentException("Schiff passt nicht ins Board (vertikal).");
        }
        this.row = row;
        this.col = col;
        this.size = size;
        this.isHorizontal = isHorizontal;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public void setShipEventPublisher(ShipEventPublisher publisher) {
        this.shipEventPublisher = publisher;
    }

    public boolean isHit(int rowIndex, int col) {
        if (isHorizontal()) {
            return getRow() == rowIndex && col >= getCol() && col < getCol() + getSize();
        } else {
            return getCol() == col && rowIndex >= getRow() && rowIndex < getRow() + getSize();
        }
    }

   public void markHit() {
    hits++;
    if (hits >= size && !sunk) {
        sunk = true;
        if (shipEventPublisher != null) {
            System.out.println("Schiff versenkt: GameID=" + gameId + ", PlayerID=" + playerId + ", ShipID=" + id);
            shipEventPublisher.publishShipSunk(gameId, playerId, id);
        }
    }
}

    public Long getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getSize() {
        return size;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public boolean isSunk() {
        return sunk;
    }

    public void setSunk(boolean sunk) {
        this.sunk = sunk;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public Long getGameId() {
        return gameId;
    }
}