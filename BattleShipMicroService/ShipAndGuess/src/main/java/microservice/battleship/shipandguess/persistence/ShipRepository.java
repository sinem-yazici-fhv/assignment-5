package microservice.battleship.shipandguess.persistence;

import microservice.battleship.shipandguess.domain.Ship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipRepository extends JpaRepository<Ship, Long> {
    List<Ship> findByGameIdAndPlayerId(Long gameId, Long opponentId);

}