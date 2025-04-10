package microservice.battleship.game.messaging;

import microservice.battleship.game.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public GameEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.err.println("FEHLER: Nachricht nicht an RabbitMQ gesendet! Grund: " + cause);
            }
        });
    }

    public void publishPlayerJoinedGame(Long gameId, Long playerId) {
        String message = gameId + ":" + playerId;
        System.out.println("Spieler " + playerId + " ist Spiel " + gameId + " beigetreten");
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.GAME_EVENTS_EXCHANGE,
            "player.joined",
            message
        );
    }

    public void publishGameOver(Long gameId, Long winnerId) {
        String message = gameId + ":" + winnerId;
        System.out.println("Spiel " + gameId + " beendet. Gewinner: " + winnerId);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.GAME_EVENTS_EXCHANGE,
            "game.over",
            message
        );
    }
}