package microservice.battleship.shipandguess.persistence;

import microservice.battleship.shipandguess.domain.Guess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuessRepository extends JpaRepository<Guess, Long> {
}