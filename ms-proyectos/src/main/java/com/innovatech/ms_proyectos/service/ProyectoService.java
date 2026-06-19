package com.innovatech.ms_proyectos.service;

import com.innovatech.ms_proyectos.dto.ProyectoExisteResponseDTO;
import com.innovatech.ms_proyectos.dto.ProyectoRequestDTO;
import com.innovatech.ms_proyectos.dto.ProyectoResponseDTO;
import com.innovatech.ms_proyectos.entity.enums.EstadoProyecto;

import java.util.List;

public interface ProyectoService {

    ProyectoResponseDTO registrarProyecto(ProyectoRequestDTO request);

    List<ProyectoResponseDTO> listarProyectos();

    ProyectoResponseDTO obtenerProyectoPorId(Long id);

    ProyectoResponseDTO actualizarProyecto(Long id, ProyectoRequestDTO request);

    ProyectoResponseDTO cambiarEstadoProyecto(Long id, EstadoProyecto estado);

    ProyectoExisteResponseDTO validarExistenciaProyecto(Long id);
}