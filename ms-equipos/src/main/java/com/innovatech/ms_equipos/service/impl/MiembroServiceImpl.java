package com.innovatech.ms_equipos.service.impl;

import com.innovatech.ms_equipos.client.proyecto.ProyectoClientFacade;
import com.innovatech.ms_equipos.dto.AsignarMiembroProyectoRequestDTO;
import com.innovatech.ms_equipos.dto.MiembroProyectoResponseDTO;
import com.innovatech.ms_equipos.dto.MiembroRequestDTO;
import com.innovatech.ms_equipos.dto.MiembroResponseDTO;
import com.innovatech.ms_equipos.entity.Miembro;
import com.innovatech.ms_equipos.entity.MiembroProyecto;
import com.innovatech.ms_equipos.exception.RecursoDuplicadoException;
import com.innovatech.ms_equipos.exception.RecursoNoEncontradoException;
import com.innovatech.ms_equipos.mapper.MiembroMapper;
import com.innovatech.ms_equipos.repository.MiembroProyectoRepository;
import com.innovatech.ms_equipos.repository.MiembroRepository;
import com.innovatech.ms_equipos.service.MiembroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MiembroServiceImpl implements MiembroService {

    private final MiembroRepository miembroRepository;
    private final MiembroProyectoRepository miembroProyectoRepository;
    private final MiembroMapper miembroMapper;
    private final ProyectoClientFacade proyectoClientFacade;

    @Override
    @Transactional
    public MiembroResponseDTO registrarMiembro(MiembroRequestDTO request) {
        if (miembroRepository.existsByIdentificador(request.getIdentificador())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un miembro con el identificador: " + request.getIdentificador()
            );
        }

        Miembro miembro = miembroMapper.toEntity(request);
        Miembro miembroGuardado = miembroRepository.save(miembro);

        return miembroMapper.toResponseDTO(miembroGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MiembroResponseDTO> listarMiembros() {
        return miembroRepository.findAll()
                .stream()
                .map(miembroMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MiembroResponseDTO obtenerMiembroPorId(Long id) {
        Miembro miembro = buscarMiembroEntityPorId(id);
        return miembroMapper.toResponseDTO(miembro);
    }

    @Override
    @Transactional
    public MiembroResponseDTO actualizarMiembro(Long id, MiembroRequestDTO request) {
        Miembro miembro = buscarMiembroEntityPorId(id);

        miembroRepository.findByIdentificador(request.getIdentificador())
                .filter(miembroExistente -> !miembroExistente.getId().equals(id))
                .ifPresent(miembroExistente -> {
                    throw new RecursoDuplicadoException(
                            "Ya existe otro miembro con el identificador: " + request.getIdentificador()
                    );
                });

        miembroMapper.actualizarEntidad(miembro, request);
        Miembro miembroActualizado = miembroRepository.save(miembro);

        return miembroMapper.toResponseDTO(miembroActualizado);
    }

    @Override
    @Transactional
    public void desactivarMiembro(Long id) {
        Miembro miembro = buscarMiembroEntityPorId(id);
        miembro.setActivo(false);
        miembroRepository.save(miembro);
    }

    @Override
    @Transactional
    public MiembroProyectoResponseDTO asignarMiembroAProyecto(AsignarMiembroProyectoRequestDTO request) {
        Miembro miembro = buscarMiembroEntityPorId(request.getMiembroId());

        if (!Boolean.TRUE.equals(miembro.getActivo())) {
            throw new RecursoNoEncontradoException(
                    "No se puede asignar un miembro inactivo a un proyecto"
            );
        }

        if (!proyectoClientFacade.existeProyecto(request.getProyectoId())) {
            throw new RecursoNoEncontradoException(
                    "No existe el proyecto con id: " + request.getProyectoId()
            );
        }

        boolean yaExisteAsignacion = miembroProyectoRepository
                .existsByMiembroIdAndProyectoId(request.getMiembroId(), request.getProyectoId());

        if (yaExisteAsignacion) {
            throw new RecursoDuplicadoException(
                    "El miembro ya esta asignado al proyecto indicado"
            );
        }

        MiembroProyecto asignacion = MiembroProyecto.builder()
                .miembro(miembro)
                .proyectoId(request.getProyectoId())
                .build();

        MiembroProyecto asignacionGuardada = miembroProyectoRepository.save(asignacion);

        return miembroMapper.toMiembroProyectoResponseDTO(asignacionGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MiembroProyectoResponseDTO> listarMiembrosPorProyecto(Long proyectoId) {
        return miembroProyectoRepository.findByProyectoId(proyectoId)
                .stream()
                .map(miembroMapper::toMiembroProyectoResponseDTO)
                .toList();
    }

    private Miembro buscarMiembroEntityPorId(Long id) {
        return miembroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Miembro no encontrado con id: " + id
                ));
    }
}
