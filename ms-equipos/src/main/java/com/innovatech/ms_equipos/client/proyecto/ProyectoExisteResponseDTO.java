package com.innovatech.ms_equipos.client.proyecto;

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