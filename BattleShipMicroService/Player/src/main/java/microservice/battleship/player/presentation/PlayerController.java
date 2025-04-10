package microservice.battleship.player.presentation;

import org.springframework.web.bind.annotation.*;
import microservice.battleship.player.application.PlayerService;
import microservice.battleship.player.domain.Player;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public Player createPlayer(@RequestParam String name, @RequestParam Long gameId) {
        return playerService.createPlayer(name, gameId);
    }

    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable Long id) {
        return playerService.getPlayer(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));
    }
}