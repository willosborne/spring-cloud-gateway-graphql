package com.ms.hackday.graphqlgateway;

import com.ms.hackday.graphqlgateway.config.RoutingConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(RoutingConfig.class)
//@EnableAutoConfiguration
public class GraphqlGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlGatewayApplication.class, args);
	}

}
