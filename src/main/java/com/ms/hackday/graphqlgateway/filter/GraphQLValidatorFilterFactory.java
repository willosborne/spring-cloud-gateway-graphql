package com.ms.hackday.graphqlgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class GraphQLValidatorFilterFactory extends AbstractGatewayFilterFactory<GraphQLValidatorFilter.Config> {
    @Override
    public GatewayFilter apply(GraphQLValidatorFilter.Config config) {
        return new GraphQLValidatorFilter(config);
    }
}
