package com.innovatech.ms_tareas.mapper;

import com.innovatech.ms_tareas.dto.TareaRequestDTO;
import com.innovatech.ms_tareas.dto.TareaResponseDTO;
import com.innovatech.ms_tareas.entity.Tarea;
import com.innovatech.ms_tareas.entity.enums.EstadoTarea;
import org.springframework.stereotype.Component;

@Component
public class TareaMapper {

    public Tarea toEntity(TareaRequestDTO dto) {
        return Tarea.builder()
                .descripcion(dto.getDescripcion())
                .proyectoId(dto.getProyectoId())
                .responsableId(dto.getResponsableId())
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoTarea.PENDING)
                .build();
    }

    public TareaResponseDTO toResponseDTO(Tarea tarea) {
        return TareaResponseDTO.builder()
                .id(tarea.getId())
                .descripcion(tarea.getDescripcion())
                .proyectoId(tarea.getProyectoId())
                .responsableId(tarea.getResponsableId())
                .estado(tarea.getEstado())
                .fechaCreacion(tarea.getFechaCreacion())
                .fechaActualizacion(tarea.getFechaActualizacion())
                .build();
    }

    public void actualizarEntidad(Tarea tarea, TareaRequestDTO dto) {
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setProyectoId(dto.getProyectoId());
        tarea.setResponsableId(dto.getResponsableId());

        if (dto.getEstado() != null) {
            tarea.setEstado(dto.getEstado());
        }
    }
}