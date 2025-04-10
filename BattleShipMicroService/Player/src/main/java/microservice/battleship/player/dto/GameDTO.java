package microservice.battleship.player.dto;

import java.util.List;

public class GameDTO {
    private Long id;
    private List<Long> playerIds;


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
}