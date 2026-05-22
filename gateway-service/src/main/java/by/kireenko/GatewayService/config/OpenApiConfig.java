package by.kireenko.GatewayService.config;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    @Lazy(false)
    public List<String> apis(RouteDefinitionLocator locator, SwaggerUiConfigParameters swaggerUiConfigParameters) {
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

        if (definitions != null) {
            definitions.stream()
                    .filter(routeDefinition -> routeDefinition.getId().endsWith("-service"))
                    .forEach(routeDefinition -> {
                        String name = routeDefinition.getId().replace("-service", "");

                        // Регистрируем группу в Swagger UI
                        swaggerUiConfigParameters.addGroup(name);
                    });
        }

        return List.of();
    }
}