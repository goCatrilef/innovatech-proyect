package com.innovatech.bff_service.client;

import com.innovatech.bff_service.dto.AsignarMiembroProyectoRequestDTO;
import com.innovatech.bff_service.dto.MiembroDTO;
import com.innovatech.bff_service.dto.MiembroProyectoDTO;
import com.innovatech.bff_service.dto.MiembroRequestDTO;
import com.innovatech.bff_service.dto.ProyectoDTO;
import com.innovatech.bff_service.dto.ProyectoRequestDTO;
import com.innovatech.bff_service.dto.TareaDTO;
import com.innovatech.bff_service.dto.TareaRequestDTO;
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

    public List<ProyectoDTO> listarProyectos() {
        try {
            return restClientBuilder.build()
                    .get()
                    .uri(apiGatewayUrl + "/api/proyectos")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ProyectoDTO>>() {});

        } catch (RestClientException ex) {
            throw new IntegracionGatewayException(
                    "No fue posible listar los proyectos"
            );
        }
    }

    public ProyectoDTO registrarProyecto(ProyectoRequestDTO request) {
        try {
            return restClientBuilder.build()
                    .post()
                    .uri(apiGatewayUrl + "/api/proyectos")
                    .body(request)
                    .retrieve()
                    .body(ProyectoDTO.class);

        } catch (RestClientException ex) {
            throw new IntegracionGatewayException(
                    "No fue posible registrar el proyecto"
            );
        }
    }

    public List<MiembroDTO> listarMiembros() {
        try {
            return restClientBuilder.build()
                    .get()
                    .uri(apiGatewayUrl + "/api/equipos/miembros")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MiembroDTO>>() {});

        } catch (RestClientException ex) {
            throw new IntegracionGatewayException(
                    "No fue posible listar los miembros"
            );
        }
    }

    public MiembroDTO registrarMiembro(MiembroRequestDTO request) {
        try {
            return restClientBuilder.build()
                    .post()
                    .uri(apiGatewayUrl + "/api/equipos/miembros")
                    .body(request)
                    .retrieve()
                    .body(MiembroDTO.class);

        } catch (RestClientException ex) {
            throw new IntegracionGatewayException(
                    "No fue posible registrar el miembro"
            );
        }
    }

    public MiembroProyectoDTO asignarMiembroAProyecto(AsignarMiembroProyectoRequestDTO request) {
        try {
            return restClientBuilder.build()
                    .post()
                    .uri(apiGatewayUrl + "/api/equipos/asignaciones")
                    .body(request)
                    .retrieve()
                    .body(MiembroProyectoDTO.class);

        } catch (RestClientException ex) {
            throw new IntegracionGatewayException(
                    "No fue posible asignar el miembro al proyecto"
            );
        }
    }

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

    public TareaDTO crearTarea(TareaRequestDTO request) {
        try {
            return restClientBuilder.build()
                    .post()
                    .uri(apiGatewayUrl + "/api/tareas")
                    .body(request)
                    .retrieve()
                    .body(TareaDTO.class);

        } catch (RestClientException ex) {
            throw new IntegracionGatewayException(
                    "No fue posible crear la tarea"
            );
        }
    }
}
