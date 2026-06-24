package com.innovatech.ms_proyectos.mapper;

import com.innovatech.ms_proyectos.dto.ProyectoRequestDTO;
import com.innovatech.ms_proyectos.dto.ProyectoResponseDTO;
import com.innovatech.ms_proyectos.entity.Proyecto;
import com.innovatech.ms_proyectos.entity.enums.EstadoProyecto;
import org.springframework.stereotype.Component;

@Component
public class ProyectoMapper {

    public Proyecto toEntity(ProyectoRequestDTO dto) {
        return Proyecto.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoProyecto.PLANIFICADO)
                .build();
    }

    public ProyectoResponseDTO toResponseDTO(Proyecto proyecto) {
        return ProyectoResponseDTO.builder()
                .id(proyecto.getId())
                .codigo(proyecto.getCodigo())
                .nombre(proyecto.getNombre())
                .descripcion(proyecto.getDescripcion())
                .fechaInicio(proyecto.getFechaInicio())
                .fechaFin(proyecto.getFechaFin())
                .estado(proyecto.getEstado())
                .fechaCreacion(proyecto.getFechaCreacion())
                .build();
    }

    public void actualizarEntidad(Proyecto proyecto, ProyectoRequestDTO dto) {
        proyecto.setCodigo(dto.getCodigo());
        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setFechaFin(dto.getFechaFin());

        if (dto.getEstado() != null) {
            proyecto.setEstado(dto.getEstado());
        }
    }
}