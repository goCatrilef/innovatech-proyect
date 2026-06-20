package com.innovatech.ms_tareas.dto;

import com.innovatech.ms_tareas.entity.enums.EstadoTarea;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TareaResponseDTO {

    private Long id;
    private String descripcion;
    private Long proyectoId;
    private Long responsableId;
    private EstadoTarea estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}