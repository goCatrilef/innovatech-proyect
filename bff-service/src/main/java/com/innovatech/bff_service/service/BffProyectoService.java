package com.innovatech.bff_service.service;

import com.innovatech.bff_service.dto.ProyectoDTO;
import com.innovatech.bff_service.dto.ProyectoRequestDTO;
import com.innovatech.bff_service.dto.ProyectoResumenDTO;

import java.util.List;

public interface BffProyectoService {

    List<ProyectoDTO> listarProyectos();

    ProyectoDTO registrarProyecto(ProyectoRequestDTO request);

    ProyectoResumenDTO obtenerResumenProyecto(Long proyectoId);
}
