package com.innovatech.ms_tareas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msTareasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-tareas API")
                        .version("v1")
                        .description("API REST para la gestión de tareas asociadas a proyectos"));
    }
}