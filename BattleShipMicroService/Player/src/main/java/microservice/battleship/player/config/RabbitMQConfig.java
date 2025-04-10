package microservice.battleship.player.config;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PLAYER_JOINED_QUEUE = "player.joined.queue";

    @Bean
    public Queue playerJoinedQueue() {
        return new Queue(PLAYER_JOINED_QUEUE, true); // durable=true
    }

}