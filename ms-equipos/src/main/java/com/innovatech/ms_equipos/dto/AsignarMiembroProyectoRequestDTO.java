package com.innovatech.ms_equipos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignarMiembroProyectoRequestDTO {

    @NotNull(message = "El id del miembro es obligatorio")
    private Long miembroId;

    @NotNull(message = "El id del proyecto es obligatorio")
    private Long proyectoId;
}