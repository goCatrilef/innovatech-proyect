package com.innovatech.bff_service.service;

import com.innovatech.bff_service.dto.AsignarMiembroProyectoRequestDTO;
import com.innovatech.bff_service.dto.MiembroDTO;
import com.innovatech.bff_service.dto.MiembroProyectoDTO;
import com.innovatech.bff_service.dto.MiembroRequestDTO;

import java.util.List;

public interface BffEquipoService {

    List<MiembroDTO> listarMiembros();

    MiembroDTO registrarMiembro(MiembroRequestDTO request);

    MiembroProyectoDTO asignarMiembroAProyecto(AsignarMiembroProyectoRequestDTO request);

    List<MiembroProyectoDTO> listarMiembrosPorProyecto(Long proyectoId);
}
