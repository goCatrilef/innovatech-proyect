package com.innovatech.ms_proyectos.dto;

import com.innovatech.ms_proyectos.entity.enums.EstadoProyecto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambiarEstadoProyectoRequestDTO {

    @NotNull(message = "El estado del proyecto es obligatorio")
    private EstadoProyecto estado;
}