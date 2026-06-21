package com.innovatech.bff_service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoResumenDTO {

    private ProyectoDTO proyecto;
    private List<MiembroProyectoDTO> miembros;
    private List<TareaDTO> tareas;

    private Integer totalTareas;
    private Integer tareasPendientes;
    private Integer tareasEnProgreso;
    private Integer tareasFinalizadas;
}