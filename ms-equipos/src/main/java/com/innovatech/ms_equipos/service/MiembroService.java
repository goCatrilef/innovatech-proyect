package com.innovatech.ms_equipos.service;

import com.innovatech.ms_equipos.dto.AsignarMiembroProyectoRequestDTO;
import com.innovatech.ms_equipos.dto.MiembroProyectoResponseDTO;
import com.innovatech.ms_equipos.dto.MiembroRequestDTO;
import com.innovatech.ms_equipos.dto.MiembroResponseDTO;

import java.util.List;

public interface MiembroService {

    MiembroResponseDTO registrarMiembro(MiembroRequestDTO request);

    List<MiembroResponseDTO> listarMiembros();

    MiembroResponseDTO obtenerMiembroPorId(Long id);

    MiembroResponseDTO actualizarMiembro(Long id, MiembroRequestDTO request);

    void desactivarMiembro(Long id);

    MiembroProyectoResponseDTO asignarMiembroAProyecto(AsignarMiembroProyectoRequestDTO request);

    List<MiembroProyectoResponseDTO> listarMiembrosPorProyecto(Long proyectoId);
}