package microservice.battleship.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;

import java.time.Duration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("game-service", r -> r.path("/game/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("gameServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback/game")))
                        .uri("lb://game-service"))

                .route("player-service", r -> r.path("/players/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("playerServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback/player")))
                        .uri("lb://player-service"))

                .route("ship-and-guess-service-ships", r -> r.path("/ships/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("shipAndGuessServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback/ship")))
                        .uri("lb://ship-and-guess-service"))

                .route("ship-and-guess-service-guesses", r -> r.path("/guesses/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("shipAndGuessServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback/guess")))
                        .uri("lb://ship-and-guess-service"))

                .route("game-details", r -> r.path("/api/game-details/**")
                        .filters(f -> f.rewritePath("/api/game-details/(?<gameId>.*)", "/api/game-details/${gameId}"))
                        .uri("lb://api-gateway"))

                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5))
                        .build())
                .build());
    }
}