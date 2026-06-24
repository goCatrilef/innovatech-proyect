package com.innovatech.ms_tareas.client.proyecto;

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