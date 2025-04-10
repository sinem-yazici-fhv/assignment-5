package microservice.battleship.shipandguess.dto;

public class ShipDTO {
    private Long id;
    private int row;
    private int col;
    private int size;
    private boolean isHorizontal;
    private boolean sunk;
    private int hits;
    private Long playerId;
    private Long gameId;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
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

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}