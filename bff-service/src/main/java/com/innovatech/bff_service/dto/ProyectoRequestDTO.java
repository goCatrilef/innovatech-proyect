package com.innovatech.bff_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoRequestDTO {

    @NotBlank(message = "El codigo del proyecto es obligatorio")
    @Size(max = 50, message = "El codigo no puede superar los 50 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripcion no puede superar los 500 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private String fechaInicio;

    private String fechaFin;

    private String estado;
}
