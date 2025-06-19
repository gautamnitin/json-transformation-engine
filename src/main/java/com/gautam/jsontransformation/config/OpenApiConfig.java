package com.gautam.jsontransformation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
It's optional if you want to customize docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JSON Transformation API")
                        .version("1.0.0")
                        .description("API for transforming JSON using Freemarker templates with schema validation and plugin support"));
    }
}
