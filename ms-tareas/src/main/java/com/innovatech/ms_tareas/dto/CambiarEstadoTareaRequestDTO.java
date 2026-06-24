package com.innovatech.ms_tareas.dto;

import com.innovatech.ms_tareas.entity.enums.EstadoTarea;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambiarEstadoTareaRequestDTO {

    @NotNull(message = "El estado de la tarea es obligatorio")
    private EstadoTarea estado;
}