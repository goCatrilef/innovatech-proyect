package com.innovatech.bff_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroProyectoDTO {

    private Long id;
    private Long miembroId;
    private String nombreMiembro;
    private String rolMiembro;
    private Long proyectoId;
    private String fechaAsignacion;
}