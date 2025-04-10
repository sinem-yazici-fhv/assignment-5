package microservice.battleship.shipandguess.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ShipEventListener {

    @RabbitListener(queues = "ship.sunk.queue")
    @Transactional
    public void handleShipSunkEvent(String message) {
        String[] parts = message.split(":");
        if (parts.length >= 3) {
            Long gameId = Long.parseLong(parts[0]);
            Long playerId = Long.parseLong(parts[1]);
            Long shipId = Long.parseLong(parts[2]);
            System.out.println("Schiff " + shipId + " von Spieler " + playerId + " wurde in Spiel " + gameId + " versenkt");
        }
    }

    @RabbitListener(queues = "game.over.queue")
    public void handleGameOver(String message) {
        String[] parts = message.split(":");
        if (parts.length >= 2) {
            Long gameId = Long.parseLong(parts[0]);
            Long winnerId = Long.parseLong(parts[1]);
            System.out.println("Spiel " + gameId + " beendet. Gewinner: " + winnerId);
        }
    }
}