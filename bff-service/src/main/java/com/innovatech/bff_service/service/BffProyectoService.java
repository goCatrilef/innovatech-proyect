package com.innovatech.bff_service.service;

import com.innovatech.bff_service.dto.ProyectoResumenDTO;

public interface BffProyectoService {

    ProyectoResumenDTO obtenerResumenProyecto(Long proyectoId);
}