package com.innovatech.bff_service.service;

import com.innovatech.bff_service.dto.TareaDTO;
import com.innovatech.bff_service.dto.TareaRequestDTO;

import java.util.List;

public interface BffTareaService {

    List<TareaDTO> listarTareasPorProyecto(Long proyectoId);

    TareaDTO crearTarea(TareaRequestDTO request);
}
