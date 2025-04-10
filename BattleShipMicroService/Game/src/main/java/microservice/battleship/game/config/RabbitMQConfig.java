package microservice.battleship.game.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String GAME_EVENTS_EXCHANGE = "game.events";
    public static final String PLAYER_JOINED_QUEUE = "player.joined.queue";
    public static final String SHIP_SUNK_QUEUE = "ship.sunk.queue";
    public static final String GAME_OVER_QUEUE = "game.over.queue";

    @Bean(name = "playerJoinedQueue")
    public Queue playerJoinedQueue() {
        return new Queue(PLAYER_JOINED_QUEUE);
    }

    @Bean(name = "shipSunkQueue")
    public Queue shipSunkQueue() {
        return new Queue(SHIP_SUNK_QUEUE);
    }

   @Bean(name = "gameOverQueue")
    public Queue gameOverQueue() {
        return new Queue(GAME_OVER_QUEUE);
    }

    @Bean
    public TopicExchange gameEventsExchange() {
        return new TopicExchange(GAME_EVENTS_EXCHANGE);
    }

    @Bean
    public Binding playerJoinedBinding(@Qualifier("playerJoinedQueue") Queue playerJoinedQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(playerJoinedQueue)
                .to(gameEventsExchange)
                .with("player.joined");
    }

    @Bean
    public Binding shipSunkBinding(@Qualifier("shipSunkQueue") Queue shipSunkQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(shipSunkQueue)
                .to(gameEventsExchange)
                .with("ship.sunk");
    }

    @Bean
    public Binding gameOverBinding(@Qualifier("gameOverQueue") Queue gameOverQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(gameOverQueue)
                .to(gameEventsExchange)
                .with("game.over");
    }
}
