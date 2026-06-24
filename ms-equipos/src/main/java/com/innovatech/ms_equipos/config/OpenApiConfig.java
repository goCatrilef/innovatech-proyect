package com.innovatech.ms_equipos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msEquiposOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-equipos API")
                        .version("v1")
                        .description("API REST para la gestión de miembros de equipos en proyectos"));
    }
}