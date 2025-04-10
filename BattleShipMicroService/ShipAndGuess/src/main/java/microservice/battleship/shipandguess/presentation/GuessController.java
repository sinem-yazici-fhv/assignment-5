package microservice.battleship.shipandguess.presentation;

import microservice.battleship.shipandguess.application.GuessService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/guesses")
public class GuessController {
    private final GuessService guessService;

    public GuessController(GuessService guessService) {
        this.guessService = guessService;
    }

    @PostMapping
    public Map<String, Object> makeGuess(
            @RequestParam Long playerId,
            @RequestParam Long gameId,
            @RequestParam int rowIndex,
            @RequestParam int col) {
        return guessService.makeGuess(playerId, gameId, rowIndex, col);
    }
}