package com.ms.hackday.graphqlgateway.filter;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

public class SchemaLoader {
    final WebClient webClient;
    final SchemaParser schemaParser;
    private SchemaGenerator schemaGenerator;
    String specPath;

    public SchemaLoader(WebClient webClient, String specPath) {
        this.webClient = webClient;
        schemaParser = new SchemaParser();
        schemaGenerator = new SchemaGenerator();
        this.specPath = specPath;
    }

    private Mono<String> getSpec() {
        return webClient.get()
                .uri(specPath)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<GraphQLSchema> getGraphQLSchema() {
        return getSpec()
                .map(this::parseSchema);
    }

    private GraphQLSchema parseSchema(String s) {
        var typeReg = schemaParser.parse(s);
        return schemaGenerator.makeExecutableSchema(typeReg, RuntimeWiring.newRuntimeWiring().build());
    }
}
