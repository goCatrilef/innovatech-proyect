package com.innovatech.ms_tareas.dto;

import com.innovatech.ms_tareas.entity.enums.EstadoTarea;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TareaRequestDTO {

    @NotBlank(message = "La descripción de la tarea es obligatoria")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;

    @NotNull(message = "El id del proyecto es obligatorio")
    private Long proyectoId;

    @NotNull(message = "El responsable de la tarea es obligatorio")
    private Long responsableId;

    private EstadoTarea estado;
}