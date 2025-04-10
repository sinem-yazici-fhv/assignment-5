package microservice.battleship.shipandguess.presentation;

import microservice.battleship.shipandguess.application.ShipService;
import microservice.battleship.shipandguess.domain.Ship;
import microservice.battleship.shipandguess.dto.ShipDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ships")
public class ShipController {
    private final ShipService shipService;

    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @PostMapping
    public Ship placeShip(
            @RequestParam Long playerId,
            @RequestParam Long gameId,
            @RequestParam int row,
            @RequestParam int col,
            @RequestParam int size,
            @RequestParam boolean isHorizontal
            ) {
        return shipService.placeShip(playerId, gameId, row, col, size, isHorizontal);
    }

     @GetMapping
     public List<ShipDTO> getShipsByGameAndPlayer(
            @RequestParam Long gameId,
            @RequestParam Long playerId) {
       return shipService.getShipsByGameAndPlayer(gameId, playerId);
    }
}