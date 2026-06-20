package com.innovatech.ms_equipos.mapper;

import com.innovatech.ms_equipos.dto.MiembroRequestDTO;
import com.innovatech.ms_equipos.dto.MiembroResponseDTO;
import com.innovatech.ms_equipos.dto.MiembroProyectoResponseDTO;
import com.innovatech.ms_equipos.entity.Miembro;
import com.innovatech.ms_equipos.entity.MiembroProyecto;
import org.springframework.stereotype.Component;

@Component
public class MiembroMapper {

    public Miembro toEntity(MiembroRequestDTO dto) {
        return Miembro.builder()
                .identificador(dto.getIdentificador())
                .nombre(dto.getNombre())
                .rol(dto.getRol())
                .email(dto.getEmail())
                .activo(dto.getActivo())
                .build();
    }

    public MiembroResponseDTO toResponseDTO(Miembro miembro) {
        return MiembroResponseDTO.builder()
                .id(miembro.getId())
                .identificador(miembro.getIdentificador())
                .nombre(miembro.getNombre())
                .rol(miembro.getRol())
                .email(miembro.getEmail())
                .activo(miembro.getActivo())
                .fechaCreacion(miembro.getFechaCreacion())
                .build();
    }

    public void actualizarEntidad(Miembro miembro, MiembroRequestDTO dto) {
        miembro.setIdentificador(dto.getIdentificador());
        miembro.setNombre(dto.getNombre());
        miembro.setRol(dto.getRol());
        miembro.setEmail(dto.getEmail());
        miembro.setActivo(dto.getActivo());
    }

    public MiembroProyectoResponseDTO toMiembroProyectoResponseDTO(MiembroProyecto miembroProyecto) {
        return MiembroProyectoResponseDTO.builder()
                .id(miembroProyecto.getId())
                .miembroId(miembroProyecto.getMiembro().getId())
                .nombreMiembro(miembroProyecto.getMiembro().getNombre())
                .rolMiembro(miembroProyecto.getMiembro().getRol())
                .proyectoId(miembroProyecto.getProyectoId())
                .fechaAsignacion(miembroProyecto.getFechaAsignacion())
                .build();
    }
}