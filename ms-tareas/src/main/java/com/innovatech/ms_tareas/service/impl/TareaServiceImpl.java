package com.innovatech.ms_tareas.service.impl;

import com.innovatech.ms_tareas.client.proyecto.ProyectoClientFacade;
import com.innovatech.ms_tareas.dto.AsignarResponsableRequestDTO;
import com.innovatech.ms_tareas.dto.CambiarEstadoTareaRequestDTO;
import com.innovatech.ms_tareas.dto.TareaRequestDTO;
import com.innovatech.ms_tareas.dto.TareaResponseDTO;
import com.innovatech.ms_tareas.entity.Tarea;
import com.innovatech.ms_tareas.entity.enums.EstadoTarea;
import com.innovatech.ms_tareas.exception.RecursoNoEncontradoException;
import com.innovatech.ms_tareas.exception.ReglaNegocioException;
import com.innovatech.ms_tareas.mapper.TareaMapper;
import com.innovatech.ms_tareas.repository.TareaRepository;
import com.innovatech.ms_tareas.service.TareaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TareaServiceImpl implements TareaService {

    private final TareaRepository tareaRepository;
    private final TareaMapper tareaMapper;
    private final ProyectoClientFacade proyectoClientFacade;

    @Override
    @Transactional
    public TareaResponseDTO crearTarea(TareaRequestDTO request) {
        validarProyectoExistente(request.getProyectoId());
        validarResponsable(request.getResponsableId());
        validarEstadoInicial(request.getEstado());

        Tarea tarea = tareaMapper.toEntity(request);
        Tarea tareaGuardada = tareaRepository.save(tarea);

        return tareaMapper.toResponseDTO(tareaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public TareaResponseDTO obtenerTareaPorId(Long id) {
        Tarea tarea = buscarTareaEntityPorId(id);
        return tareaMapper.toResponseDTO(tarea);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaResponseDTO> listarTareasPorProyecto(Long proyectoId) {
        return tareaRepository.findByProyectoId(proyectoId)
                .stream()
                .map(tareaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaResponseDTO> listarTareasPorEstado(EstadoTarea estado) {
        return tareaRepository.findByEstado(estado)
                .stream()
                .map(tareaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaResponseDTO> listarTareasPorResponsable(Long responsableId) {
        return tareaRepository.findByResponsableId(responsableId)
                .stream()
                .map(tareaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public TareaResponseDTO actualizarTarea(Long id, TareaRequestDTO request) {
        Tarea tarea = buscarTareaEntityPorId(id);

        validarProyectoExistente(request.getProyectoId());
        validarResponsable(request.getResponsableId());

        if (request.getEstado() != null && !request.getEstado().equals(tarea.getEstado())) {
            validarTransicionEstado(tarea.getEstado(), request.getEstado());
        }

        tareaMapper.actualizarEntidad(tarea, request);
        Tarea tareaActualizada = tareaRepository.save(tarea);

        return tareaMapper.toResponseDTO(tareaActualizada);
    }

    @Override
    @Transactional
    public TareaResponseDTO cambiarEstadoTarea(Long id, CambiarEstadoTareaRequestDTO request) {
        Tarea tarea = buscarTareaEntityPorId(id);
        EstadoTarea nuevoEstado = request.getEstado();

        validarTransicionEstado(tarea.getEstado(), nuevoEstado);

        tarea.setEstado(nuevoEstado);
        Tarea tareaActualizada = tareaRepository.save(tarea);

        return tareaMapper.toResponseDTO(tareaActualizada);
    }

    @Override
    @Transactional
    public TareaResponseDTO asignarResponsable(Long id, AsignarResponsableRequestDTO request) {
        Tarea tarea = buscarTareaEntityPorId(id);

        validarResponsable(request.getResponsableId());

        tarea.setResponsableId(request.getResponsableId());
        Tarea tareaActualizada = tareaRepository.save(tarea);

        return tareaMapper.toResponseDTO(tareaActualizada);
    }

    private Tarea buscarTareaEntityPorId(Long id) {
        return tareaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tarea no encontrada con id: " + id
                ));
    }

    private void validarProyectoExistente(Long proyectoId) {
        if (proyectoId == null) {
            throw new ReglaNegocioException("La tarea debe estar asociada a un proyecto");
        }

        if (!proyectoClientFacade.existeProyecto(proyectoId)) {
            throw new RecursoNoEncontradoException(
                    "No existe el proyecto asociado con id: " + proyectoId
            );
        }
    }

    private void validarResponsable(Long responsableId) {
        if (responsableId == null) {
            throw new ReglaNegocioException("La tarea debe tener un responsable asignado");
        }

        if (responsableId <= 0) {
            throw new ReglaNegocioException("El id del responsable debe ser mayor a cero");
        }
    }

    private void validarEstadoInicial(EstadoTarea estado) {
        if (estado != null && !EstadoTarea.PENDING.equals(estado)) {
            throw new ReglaNegocioException(
                    "Una tarea nueva debe iniciar en estado PENDING"
            );
        }
    }

    private void validarTransicionEstado(EstadoTarea estadoActual, EstadoTarea nuevoEstado) {
        if (nuevoEstado == null) {
            throw new ReglaNegocioException("El nuevo estado de la tarea es obligatorio");
        }

        if (estadoActual.equals(nuevoEstado)) {
            return;
        }

        if (EstadoTarea.PENDING.equals(estadoActual)
                && EstadoTarea.IN_PROGRESS.equals(nuevoEstado)) {
            return;
        }

        if (EstadoTarea.IN_PROGRESS.equals(estadoActual)
                && EstadoTarea.DONE.equals(nuevoEstado)) {
            return;
        }

        throw new ReglaNegocioException(
                "Transición de estado no permitida: " + estadoActual + " -> " + nuevoEstado
        );
    }
}