package ru.local.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ProxyApplication {

    private static final String MONOLITH_URL = "monolith.url";
    private static final String MOVIES_SERVICE_URL = "movies.service.url";
    private static final String EVENTS_SERVICE_URL = "events.service.url";
    private static final String GRADUAL_MIGRATION = "gradual.migration";
    private static final String MOVIES_MIGRATION_PERCENT = "movies.migration.percent";

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(
        RouteLocatorBuilder builder, Environment environment) {

        final var monolithUrl = environment.getProperty(MONOLITH_URL);
        final var eventServiceUrl = environment.getProperty(EVENTS_SERVICE_URL);
        final var moviesServiceUrl = environment.getProperty(MOVIES_SERVICE_URL);
        final var gradualMigrationEnabled =
            environment.getProperty(GRADUAL_MIGRATION, Boolean.class, Boolean.FALSE);
        final var migrationPercent = gradualMigrationEnabled ?
            environment.getProperty(MOVIES_MIGRATION_PERCENT, Integer.class, 100) : 100;
        return builder.routes()
            .route("movies-ms", r ->
                r.path("/api/movies")
                    .and()
                    .weight("movies", migrationPercent)
                    .uri(moviesServiceUrl))
            .route("movies-monolith", r ->
                r.path("/api/movies")
                    .and()
                    .weight("movies", 100 - migrationPercent)
                    .uri(monolithUrl))
            .route("events", r ->
                r.path("/api/events")
                    .uri(eventServiceUrl))
            .build();
    }
}
