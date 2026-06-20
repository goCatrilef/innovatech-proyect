package com.innovatech.ms_equipos.client.proyecto;

import com.innovatech.ms_equipos.exception.IntegracionProyectoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class ProyectoClientFacadeImpl implements ProyectoClientFacade {

    private final RestClient.Builder restClientBuilder;

    @Value("${app.proyectos-service.url}")
    private String proyectosServiceUrl;

    @Override
    public boolean existeProyecto(Long proyectoId) {
        try {
            ProyectoExisteResponseDTO response = restClientBuilder.build()
                    .get()
                    .uri(proyectosServiceUrl + "/api/proyectos/{id}/existe", proyectoId)
                    .retrieve()
                    .body(ProyectoExisteResponseDTO.class);

            return response != null && Boolean.TRUE.equals(response.getExiste());

        } catch (HttpClientErrorException.NotFound ex) {
            return false;

        } catch (RestClientException ex) {
            throw new IntegracionProyectoException(
                    "No fue posible validar el proyecto con id: " + proyectoId
            );
        }
    }
}