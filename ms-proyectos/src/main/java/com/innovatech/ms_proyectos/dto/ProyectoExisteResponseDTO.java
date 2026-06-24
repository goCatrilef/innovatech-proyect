package com.innovatech.ms_proyectos.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoExisteResponseDTO {

    private Long proyectoId;
    private Boolean existe;
}

