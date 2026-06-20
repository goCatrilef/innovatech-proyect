package com.innovatech.ms_tareas.service;

import com.innovatech.ms_tareas.dto.AsignarResponsableRequestDTO;
import com.innovatech.ms_tareas.dto.CambiarEstadoTareaRequestDTO;
import com.innovatech.ms_tareas.dto.TareaRequestDTO;
import com.innovatech.ms_tareas.dto.TareaResponseDTO;
import com.innovatech.ms_tareas.entity.enums.EstadoTarea;

import java.util.List;

public interface TareaService {

    TareaResponseDTO crearTarea(TareaRequestDTO request);

    TareaResponseDTO obtenerTareaPorId(Long id);

    List<TareaResponseDTO> listarTareasPorProyecto(Long proyectoId);

    List<TareaResponseDTO> listarTareasPorEstado(EstadoTarea estado);

    List<TareaResponseDTO> listarTareasPorResponsable(Long responsableId);

    TareaResponseDTO actualizarTarea(Long id, TareaRequestDTO request);

    TareaResponseDTO cambiarEstadoTarea(Long id, CambiarEstadoTareaRequestDTO request);

    TareaResponseDTO asignarResponsable(Long id, AsignarResponsableRequestDTO request);
}
