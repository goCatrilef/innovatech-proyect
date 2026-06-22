package com.innovatech.bff_service.service.impl;

import com.innovatech.bff_service.client.ApiGatewayClient;
import com.innovatech.bff_service.dto.MiembroProyectoDTO;
import com.innovatech.bff_service.dto.ProyectoDTO;
import com.innovatech.bff_service.dto.ProyectoRequestDTO;
import com.innovatech.bff_service.dto.ProyectoResumenDTO;
import com.innovatech.bff_service.dto.TareaDTO;
import com.innovatech.bff_service.service.BffProyectoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BffProyectoServiceImpl implements BffProyectoService {

    private final ApiGatewayClient apiGatewayClient;

    @Override
    public List<ProyectoDTO> listarProyectos() {
        return apiGatewayClient.listarProyectos();
    }

    @Override
    public ProyectoDTO registrarProyecto(ProyectoRequestDTO request) {
        return apiGatewayClient.registrarProyecto(request);
    }

    @Override
    public ProyectoResumenDTO obtenerResumenProyecto(Long proyectoId) {
        ProyectoDTO proyecto = apiGatewayClient.obtenerProyectoPorId(proyectoId);

        List<MiembroProyectoDTO> miembros = apiGatewayClient.obtenerMiembrosPorProyecto(proyectoId);
        List<TareaDTO> tareas = apiGatewayClient.obtenerTareasPorProyecto(proyectoId);

        int totalTareas = tareas.size();
        int tareasPendientes = contarPorEstado(tareas, "PENDING");
        int tareasEnProgreso = contarPorEstado(tareas, "IN_PROGRESS");
        int tareasFinalizadas = contarPorEstado(tareas, "DONE");

        return ProyectoResumenDTO.builder()
                .proyecto(proyecto)
                .miembros(miembros)
                .tareas(tareas)
                .totalTareas(totalTareas)
                .tareasPendientes(tareasPendientes)
                .tareasEnProgreso(tareasEnProgreso)
                .tareasFinalizadas(tareasFinalizadas)
                .build();
    }

    private int contarPorEstado(List<TareaDTO> tareas, String estado) {
        return (int) tareas.stream()
                .filter(tarea -> estado.equalsIgnoreCase(tarea.getEstado()))
                .count();
    }
}
