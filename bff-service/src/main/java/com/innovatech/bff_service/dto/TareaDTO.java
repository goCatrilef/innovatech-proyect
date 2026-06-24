package com.innovatech.bff_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TareaDTO {

    private Long id;
    private String descripcion;
    private Long proyectoId;
    private Long responsableId;
    private String estado;
    private String fechaCreacion;
    private String fechaActualizacion;
}