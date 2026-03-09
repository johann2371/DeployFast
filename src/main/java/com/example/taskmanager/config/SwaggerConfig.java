package com.example.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuration de la documentation Swagger / OpenAPI
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Informations générales de l'API
                .info(new Info()
                        .title("TaskManager API")
                        .version("1.0")
                        .description("API REST de gestion des tâches — Projet EADL4 2026")
                        .contact(new Contact()
                                .name("DeployFast Team")
                                .email("contact@deployfast.com")
                        )
                )
                // Ajoute le bouton "Authorize" pour tester avec JWT
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Token", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Entrez votre token JWT ici")
                        )
                );
    }
}
