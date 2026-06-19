package com.innovatech.ms_equipos.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroResponseDTO {

    private Long id;
    private String identificador;
    private String nombre;
    private String rol;
    private String email;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}