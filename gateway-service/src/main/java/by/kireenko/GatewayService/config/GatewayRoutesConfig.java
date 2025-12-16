package by.kireenko.GatewayService.config;

import by.kireenko.GatewayService.filters.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayRoutesConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-route", r -> r.path("/api/auth/**", "/api/users/**", "/api/account/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://user-service"))
                .route("car-service-route", r -> r.path("/api/cars", "/api/cars/{id}", "/api/cars/available", "/api/cars/batch", "/api/cars/{id}/reserve", "/api/cars/{id}/release")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://car-service"))
                .route("car-details-service-route", r -> r.path("/api/cars/{id}/details/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://car-details-service"))
                .route("booking-service-route", r -> r.path("/api/bookings/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://booking-service"))
                .build();
    }
}