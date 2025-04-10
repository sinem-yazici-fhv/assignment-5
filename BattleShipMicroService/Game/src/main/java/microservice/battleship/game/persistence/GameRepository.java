package microservice.battleship.game.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import microservice.battleship.game.Domain.Game;


public interface GameRepository extends JpaRepository<Game, Long> {
}
