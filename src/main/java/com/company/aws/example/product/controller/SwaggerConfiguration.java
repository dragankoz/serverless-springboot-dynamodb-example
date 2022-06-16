package com.company.aws.example.product.controller;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfiguration {
    private static final String LICENSE_NAME = "Apache 2.0";
    private static final String LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0";
    private static final String DOCUMENTATION_URL = "https://tba-dev-portal.company.com";

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("products")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI customerServiceOpenAPI(
            @Value("${app.project.version}") String version,
            @Value("${app.project.name}") String name,
            @Value("${app.project.description}") String description) {
        return new OpenAPI()
                .info(new Info().title(name)
                        .description(description)
                        .version(version)
                        .license(new License().name(LICENSE_NAME).url(LICENSE_URL)))
                .externalDocs(new ExternalDocumentation()
                        .description(description)
                        .url(DOCUMENTATION_URL));
    }
}
