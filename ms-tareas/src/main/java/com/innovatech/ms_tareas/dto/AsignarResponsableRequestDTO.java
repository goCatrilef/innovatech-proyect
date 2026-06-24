package com.innovatech.ms_tareas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignarResponsableRequestDTO {

    @NotNull(message = "El responsable de la tarea es obligatorio")
    private Long responsableId;
}