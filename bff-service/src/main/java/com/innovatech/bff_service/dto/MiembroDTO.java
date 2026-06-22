package com.innovatech.bff_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroDTO {

    private Long id;
    private String identificador;
    private String nombre;
    private String rol;
    private String email;
    private Boolean activo;
    private String fechaCreacion;
}
