package com.innovatech.bff_service.dto;

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

    @NotBlank(message = "La descripcion de la tarea es obligatoria")
    @Size(max = 500, message = "La descripcion no puede superar los 500 caracteres")
    private String descripcion;

    @NotNull(message = "El id del proyecto es obligatorio")
    private Long proyectoId;

    @NotNull(message = "El responsable de la tarea es obligatorio")
    private Long responsableId;

    private String estado;
}
