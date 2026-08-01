package com.olympic.dakar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI olympicOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Olympic Management System API")
                        .description("API REST pour la gestion des athlètes, disciplines, épreuves et résultats des Jeux Olympiques de Dakar")
                        .version("v1")
                        .contact(new Contact().name("Olympic Management System")));
    }
}
