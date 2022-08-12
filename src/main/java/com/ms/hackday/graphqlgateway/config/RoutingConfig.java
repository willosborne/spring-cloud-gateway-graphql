package com.ms.hackday.graphqlgateway.config;

import com.ms.hackday.graphqlgateway.filter.GraphQLValidatorFilter;
import com.ms.hackday.graphqlgateway.filter.GraphQLValidatorFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RoutingConfig {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/graphql")
                        .filters(f -> f
                                .modifyRequestBody(String.class, String.class, (serverWebExchange, body) -> {
                                    serverWebExchange.getAttributes().put("BODY", body);
                                    return Mono.just(body);
                                })
                                .filter(new GraphQLValidatorFilterFactory().apply(
                                        GraphQLValidatorFilter.Config.builder()
                                                .specPath("/schema.graphqls")
                                                .build()
                                ))
                        )
                        .uri("http://localhost:8080")
                )
                .route(p -> p
                        .path("/graphiql")
                        .uri("http://localhost:8080")
                )
                .build();
    }
}
