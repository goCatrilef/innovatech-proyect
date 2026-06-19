package com.innovatech.ms_proyectos.dto;

import com.innovatech.ms_proyectos.entity.enums.EstadoProyecto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoResponseDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoProyecto estado;
    private LocalDateTime fechaCreacion;
}