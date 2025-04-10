package microservice.battleship.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/game")
    public Mono<Map<String, String>> gameServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Game service is currently unavailable. Please try again later.");
        return Mono.just(response);
    }

    @GetMapping("/player")
    public Mono<Map<String, String>> playerServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Player service is currently unavailable. Please try again later.");
        return Mono.just(response);
    }

    @GetMapping("/ship")
    public Mono<Map<String, String>> shipServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Ship service is currently unavailable. Please try again later.");
        return Mono.just(response);
    }

    @GetMapping("/guess")
    public Mono<Map<String, String>> guessServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Guess service is currently unavailable. Please try again later.");
        return Mono.just(response);
    }

    @GetMapping("/game-details")
    public Mono<Map<String, String>> gameDetailsFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Game details are currently unavailable. Please try again later.");
        return Mono.just(response);
    }
}