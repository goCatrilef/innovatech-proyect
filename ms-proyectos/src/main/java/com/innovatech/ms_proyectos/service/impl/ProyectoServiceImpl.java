package com.innovatech.ms_proyectos.service.impl;

import com.innovatech.ms_proyectos.dto.ProyectoExisteResponseDTO;
import com.innovatech.ms_proyectos.dto.ProyectoRequestDTO;
import com.innovatech.ms_proyectos.dto.ProyectoResponseDTO;
import com.innovatech.ms_proyectos.entity.Proyecto;
import com.innovatech.ms_proyectos.entity.enums.EstadoProyecto;
import com.innovatech.ms_proyectos.exception.RecursoDuplicadoException;
import com.innovatech.ms_proyectos.exception.RecursoNoEncontradoException;
import com.innovatech.ms_proyectos.exception.ReglaNegocioException;
import com.innovatech.ms_proyectos.mapper.ProyectoMapper;
import com.innovatech.ms_proyectos.repository.ProyectoRepository;
import com.innovatech.ms_proyectos.service.ProyectoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProyectoServiceImpl implements ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final ProyectoMapper proyectoMapper;

    @Override
    @Transactional
    public ProyectoResponseDTO registrarProyecto(ProyectoRequestDTO request) {
        validarFechas(request.getFechaInicio(), request.getFechaFin());

        if (proyectoRepository.existsByCodigo(request.getCodigo())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un proyecto con el código: " + request.getCodigo()
            );
        }

        Proyecto proyecto = proyectoMapper.toEntity(request);
        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);

        return proyectoMapper.toResponseDTO(proyectoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProyectoResponseDTO> listarProyectos() {
        return proyectoRepository.findAll()
                .stream()
                .map(proyectoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoResponseDTO obtenerProyectoPorId(Long id) {
        Proyecto proyecto = buscarProyectoEntityPorId(id);
        return proyectoMapper.toResponseDTO(proyecto);
    }

    @Override
    @Transactional
    public ProyectoResponseDTO actualizarProyecto(Long id, ProyectoRequestDTO request) {
        validarFechas(request.getFechaInicio(), request.getFechaFin());

        Proyecto proyecto = buscarProyectoEntityPorId(id);

        proyectoRepository.findByCodigo(request.getCodigo())
                .filter(proyectoExistente -> !proyectoExistente.getId().equals(id))
                .ifPresent(proyectoExistente -> {
                    throw new RecursoDuplicadoException(
                            "Ya existe otro proyecto con el código: " + request.getCodigo()
                    );
                });

        proyectoMapper.actualizarEntidad(proyecto, request);
        Proyecto proyectoActualizado = proyectoRepository.save(proyecto);

        return proyectoMapper.toResponseDTO(proyectoActualizado);
    }

    @Override
    @Transactional
    public ProyectoResponseDTO cambiarEstadoProyecto(Long id, EstadoProyecto estado) {
        Proyecto proyecto = buscarProyectoEntityPorId(id);
        proyecto.setEstado(estado);

        Proyecto proyectoActualizado = proyectoRepository.save(proyecto);

        return proyectoMapper.toResponseDTO(proyectoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoExisteResponseDTO validarExistenciaProyecto(Long id) {
        boolean existe = proyectoRepository.existsById(id);

        return ProyectoExisteResponseDTO.builder()
                .proyectoId(id)
                .existe(existe)
                .build();
    }

    private Proyecto buscarProyectoEntityPorId(Long id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Proyecto no encontrado con id: " + id
                ));
    }

    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null) {
            throw new ReglaNegocioException("La fecha de inicio es obligatoria");
        }

        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new ReglaNegocioException(
                    "La fecha de término no puede ser anterior a la fecha de inicio"
            );
        }
    }
}