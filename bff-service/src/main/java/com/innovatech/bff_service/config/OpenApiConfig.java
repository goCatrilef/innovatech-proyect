package com.innovatech.bff_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bffServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("bff-service API")
                        .version("v1")
                        .description("Backend For Frontend para la plataforma Innovatech"));
    }
}