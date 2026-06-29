package com.innovatech.ms_tareas.mapper;

import com.innovatech.ms_tareas.dto.TareaRequestDTO;
import com.innovatech.ms_tareas.dto.TareaResponseDTO;
import com.innovatech.ms_tareas.entity.Tarea;
import com.innovatech.ms_tareas.entity.enums.EstadoTarea;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TareaMapperTest {

    private final TareaMapper tareaMapper = new TareaMapper();

    @Test
    void toEntity_debeAsignarEstadoPendingCuandoEstadoVieneNulo() {
        TareaRequestDTO request = TareaRequestDTO.builder()
                .descripcion("Mapper con estado nulo")
                .proyectoId(1L)
                .responsableId(10L)
                .estado(null)
                .build();

        Tarea tarea = tareaMapper.toEntity(request);

        assertEquals("Mapper con estado nulo", tarea.getDescripcion());
        assertEquals(1L, tarea.getProyectoId());
        assertEquals(10L, tarea.getResponsableId());
        assertEquals(EstadoTarea.PENDING, tarea.getEstado());
    }

    @Test
    void toResponseDTO_debeMapearTodosLosCampos() {
        LocalDateTime fechaCreacion = LocalDateTime.now();

        Tarea tarea = Tarea.builder()
                .id(1L)
                .descripcion("Tarea mapeada")
                .proyectoId(1L)
                .responsableId(10L)
                .estado(EstadoTarea.IN_PROGRESS)
                .fechaCreacion(fechaCreacion)
                .build();

        TareaResponseDTO response = tareaMapper.toResponseDTO(tarea);

        assertEquals(1L, response.getId());
        assertEquals("Tarea mapeada", response.getDescripcion());
        assertEquals(1L, response.getProyectoId());
        assertEquals(10L, response.getResponsableId());
        assertEquals(EstadoTarea.IN_PROGRESS, response.getEstado());
        assertEquals(fechaCreacion, response.getFechaCreacion());
    }

    @Test
    void actualizarEntidad_noDebeModificarEstadoCuandoEstadoVieneNulo() {
        Tarea tarea = Tarea.builder()
                .id(1L)
                .descripcion("Descripción anterior")
                .proyectoId(1L)
                .responsableId(10L)
                .estado(EstadoTarea.PENDING)
                .build();

        TareaRequestDTO request = TareaRequestDTO.builder()
                .descripcion("Descripción nueva")
                .proyectoId(2L)
                .responsableId(20L)
                .estado(null)
                .build();

        tareaMapper.actualizarEntidad(tarea, request);

        assertEquals("Descripción nueva", tarea.getDescripcion());
        assertEquals(2L, tarea.getProyectoId());
        assertEquals(20L, tarea.getResponsableId());
        assertEquals(EstadoTarea.PENDING, tarea.getEstado());
    }

    @Test
    void actualizarEntidad_debeModificarEstadoCuandoEstadoVieneInformado() {
        Tarea tarea = Tarea.builder()
                .id(1L)
                .descripcion("Descripción anterior")
                .proyectoId(1L)
                .responsableId(10L)
                .estado(EstadoTarea.PENDING)
                .build();

        TareaRequestDTO request = TareaRequestDTO.builder()
                .descripcion("Descripción actualizada")
                .proyectoId(1L)
                .responsableId(10L)
                .estado(EstadoTarea.IN_PROGRESS)
                .build();

        tareaMapper.actualizarEntidad(tarea, request);

        assertEquals("Descripción actualizada", tarea.getDescripcion());
        assertEquals(EstadoTarea.IN_PROGRESS, tarea.getEstado());
    }
}