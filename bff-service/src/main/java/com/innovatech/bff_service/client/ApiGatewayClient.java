package com.innovatech.bff_service.client;

import com.innovatech.bff_service.dto.MiembroProyectoDTO;
import com.innovatech.bff_service.dto.ProyectoDTO;
import com.innovatech.bff_service.dto.TareaDTO;
import com.innovatech.bff_service.exception.IntegracionGatewayException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiGatewayClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${app.api-gateway.url}")
    private String apiGatewayUrl;

    public ProyectoDTO obtenerProyectoPorId(Long proyectoId) {
        try {
            return restClientBuilder.build()
                    .get()
                    .uri(apiGatewayUrl + "/api/proyectos/{id}", proyectoId)
                    .retrieve()
                    .body(ProyectoDTO.class);

        } catch (RestClientException ex) {
            throw new IntegracionGatewayException(
                    "No fue posible obtener el proyecto con id: " + proyectoId
            );
        }
    }

    public List<MiembroProyectoDTO> obtenerMiembrosPorProyecto(Long proyectoId) {
        try {
            return restClientBuilder.build()
                    .get()
                    .uri(apiGatewayUrl + "/api/equipos/proyectos/{proyectoId}/miembros", proyectoId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MiembroProyectoDTO>>() {});

        } catch (RestClientException ex) {
            throw new IntegracionGatewayException(
                    "No fue posible obtener los miembros del proyecto con id: " + proyectoId
            );
        }
    }

    public List<TareaDTO> obtenerTareasPorProyecto(Long proyectoId) {
        try {
            return restClientBuilder.build()
                    .get()
                    .uri(apiGatewayUrl + "/api/tareas/proyecto/{proyectoId}", proyectoId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TareaDTO>>() {});

        } catch (RestClientException ex) {
            throw new IntegracionGatewayException(
                    "No fue posible obtener las tareas del proyecto con id: " + proyectoId
            );
        }
    }
}