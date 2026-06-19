package com.innovatech.ms_equipos.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroProyectoResponseDTO {

    private Long id;
    private Long miembroId;
    private String nombreMiembro;
    private String rolMiembro;
    private Long proyectoId;
    private LocalDateTime fechaAsignacion;
}