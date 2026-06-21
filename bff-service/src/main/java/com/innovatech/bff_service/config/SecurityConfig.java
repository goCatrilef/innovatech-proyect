package com.innovatech.bff_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/actuator/health",
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                .requestMatchers("/api/bff/admin/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers("/api/bff/proyectos/**")
                                                .hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER", "USER")

                                                .anyRequest()
                                                .authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                                                jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                                .build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
                return converter;
        }
}