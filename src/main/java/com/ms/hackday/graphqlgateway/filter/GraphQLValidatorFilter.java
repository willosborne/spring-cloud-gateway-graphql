package com.ms.hackday.graphqlgateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ParseAndValidate;
import graphql.ParseAndValidateResult;
import graphql.schema.GraphQLSchema;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriBuilderFactory;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

public class GraphQLValidatorFilter implements GatewayFilter {
    @Getter
    @Builder
    public static class Config {
        String specPath;
    }

    final String specPath;

    public GraphQLValidatorFilter(Config config) {
//    schemaparser
//            parser file
//                    makeexecutableschema
//                            parseandvalidate
        this.specPath = config.getSpecPath();
    }

    SchemaLoader getSchemaLoader(String baseUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        return new SchemaLoader(webClient, specPath);
    }

//    private String retrieveSpec(Config config) {
//
//    }

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        URI requestUri = route.getUri();
        String requestBody = exchange.getAttribute("BODY");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        String query = jsonNode.at("/query").textValue();

        ExecutionInput input = ExecutionInput.newExecutionInput(query)
                .build();
//        String query = input.getQuery();
        final SchemaLoader schemaLoader = getSchemaLoader(requestUri.resolve("/").toString());

        return schemaLoader.getGraphQLSchema()
                .flatMap(schema -> {
                    ParseAndValidateResult parseAndValidateResult = ParseAndValidate.parseAndValidate(schema, input);
                    if (parseAndValidateResult.isFailure())
                        return Mono.error(new IllegalArgumentException(parseAndValidateResult.getSyntaxException()));
                    return chain.filter(exchange);
                });
    }
}
