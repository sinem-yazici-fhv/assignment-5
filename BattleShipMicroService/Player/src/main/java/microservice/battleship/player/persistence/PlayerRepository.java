package microservice.battleship.player.persistence;

import microservice.battleship.player.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlayerRepository extends JpaRepository<Player, Long> {
}
