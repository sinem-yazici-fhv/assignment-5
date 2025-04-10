package microservice.battleship.shipandguess.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String GAME_EVENTS_EXCHANGE = "game.events";
    public static final String SHIP_SUNK_QUEUE = "ship.sunk.queue";
    public static final String GAME_OVER_QUEUE = "game.over.queue";

    @Bean
    public TopicExchange gameEventsExchange() {
        return new TopicExchange(GAME_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue shipSunkQueue() {
        Queue queue = new Queue(SHIP_SUNK_QUEUE);
        System.out.println("🔵 Created ship.sunk.queue: " + queue);
        return queue;
    }

    @Bean
    public Queue gameOverQueue() {
        Queue queue = new Queue(GAME_OVER_QUEUE);
        System.out.println("🔵 Created game.over.queue: " + queue);
        return queue;
    }

    @Bean
    public Binding shipSunkBinding(Queue shipSunkQueue, TopicExchange gameEventsExchange) {
        Binding binding = BindingBuilder.bind(shipSunkQueue)
                .to(gameEventsExchange)
                .with("ship.sunk");
        System.out.println("🔵 Created binding for ship.sunk: " + binding);
        return binding;
    }

    @Bean
    public Binding gameOverBinding(Queue gameOverQueue, TopicExchange gameEventsExchange) {
        Binding binding = BindingBuilder.bind(gameOverQueue)
                .to(gameEventsExchange)
                .with("game.over");
        System.out.println("🔵 Created binding for game.over: " + binding);
        return binding;
    }
}