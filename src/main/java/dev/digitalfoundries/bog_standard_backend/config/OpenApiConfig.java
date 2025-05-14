package dev.digitalfoundries.bog_standard_backend.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
    @Configuration
    public class OpenApiConfig {

        @Bean
        public GroupedOpenApi bogApi() {
            System.out.println("GroupedOpenApi class = " + GroupedOpenApi.class);
            return GroupedOpenApi.builder()
                    .group("bog-api")
                    .packagesToScan("dev.digitalfoundries.bog_backend.controller")
                    .build();
        }
    }