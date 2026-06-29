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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TareaServiceImplTest {

        @Mock
        private TareaRepository tareaRepository;

        @Mock
        private TareaMapper tareaMapper;

        @Mock
        private ProyectoClientFacade proyectoClientFacade;

        @InjectMocks
        private TareaServiceImpl tareaService;

        @Test
        void crearTarea_debeGuardarTareaCuandoProyectoExisteYDatosSonValidos() {
                TareaRequestDTO request = TareaRequestDTO.builder()
                                .descripcion("Diseñar pantalla de gestión de tareas")
                                .proyectoId(1L)
                                .responsableId(10L)
                                .estado(EstadoTarea.PENDING)
                                .build();

                Tarea tareaSinGuardar = Tarea.builder()
                                .descripcion(request.getDescripcion())
                                .proyectoId(request.getProyectoId())
                                .responsableId(request.getResponsableId())
                                .estado(request.getEstado())
                                .build();

                Tarea tareaGuardada = Tarea.builder()
                                .id(100L)
                                .descripcion(request.getDescripcion())
                                .proyectoId(request.getProyectoId())
                                .responsableId(request.getResponsableId())
                                .estado(EstadoTarea.PENDING)
                                .build();

                TareaResponseDTO responseEsperada = TareaResponseDTO.builder()
                                .id(100L)
                                .descripcion(request.getDescripcion())
                                .proyectoId(request.getProyectoId())
                                .responsableId(request.getResponsableId())
                                .estado(EstadoTarea.PENDING)
                                .build();

                when(proyectoClientFacade.existeProyecto(1L)).thenReturn(true);
                when(tareaMapper.toEntity(request)).thenReturn(tareaSinGuardar);
                when(tareaRepository.save(tareaSinGuardar)).thenReturn(tareaGuardada);
                when(tareaMapper.toResponseDTO(tareaGuardada)).thenReturn(responseEsperada);

                TareaResponseDTO resultado = tareaService.crearTarea(request);

                assertNotNull(resultado);
                assertEquals(100L, resultado.getId());
                assertEquals("Diseñar pantalla de gestión de tareas", resultado.getDescripcion());
                assertEquals(1L, resultado.getProyectoId());
                assertEquals(10L, resultado.getResponsableId());
                assertEquals(EstadoTarea.PENDING, resultado.getEstado());

                verify(proyectoClientFacade).existeProyecto(1L);
                verify(tareaRepository).save(tareaSinGuardar);
        }

        @Test
        void crearTarea_debeLanzarExcepcionCuandoProyectoNoExiste() {

                TareaRequestDTO request = TareaRequestDTO.builder()
                                .descripcion("Crear tarea con proyecto inválido")
                                .proyectoId(999L)
                                .responsableId(10L)
                                .estado(EstadoTarea.PENDING)
                                .build();

                when(proyectoClientFacade.existeProyecto(999L)).thenReturn(false);

                RecursoNoEncontradoException exception = assertThrows(
                                RecursoNoEncontradoException.class,
                                () -> tareaService.crearTarea(request));

                assertEquals("No existe el proyecto asociado con id: 999", exception.getMessage());

                verify(proyectoClientFacade).existeProyecto(999L);
                verifyNoInteractions(tareaMapper);
                verify(tareaRepository, never()).save(any());
        }

        @Test
        void cambiarEstadoTarea_debeRechazarTransicionNoPermitidaDePendienteADone() {

                Long tareaId = 1L;

                Tarea tareaExistente = Tarea.builder()
                                .id(tareaId)
                                .descripcion("Tarea pendiente")
                                .proyectoId(1L)
                                .responsableId(10L)
                                .estado(EstadoTarea.PENDING)
                                .build();

                CambiarEstadoTareaRequestDTO request = new CambiarEstadoTareaRequestDTO();
                request.setEstado(EstadoTarea.DONE);

                when(tareaRepository.findById(tareaId)).thenReturn(Optional.of(tareaExistente));

                ReglaNegocioException exception = assertThrows(
                                ReglaNegocioException.class,
                                () -> tareaService.cambiarEstadoTarea(tareaId, request));

                assertEquals(
                                "Transición de estado no permitida: PENDING -> DONE",
                                exception.getMessage());

                verify(tareaRepository).findById(tareaId);
                verify(tareaRepository, never()).save(any());

                ArgumentCaptor<Tarea> captor = ArgumentCaptor.forClass(Tarea.class);
                verify(tareaRepository, never()).save(captor.capture());
        }

        @Test
        void crearTarea_debeLanzarExcepcionCuandoProyectoIdEsNulo() {
                TareaRequestDTO request = TareaRequestDTO.builder()
                                .descripcion("Tarea sin proyecto")
                                .proyectoId(null)
                                .responsableId(10L)
                                .estado(EstadoTarea.PENDING)
                                .build();

                ReglaNegocioException exception = assertThrows(
                                ReglaNegocioException.class,
                                () -> tareaService.crearTarea(request));

                assertEquals("La tarea debe estar asociada a un proyecto", exception.getMessage());

                verifyNoInteractions(proyectoClientFacade);
                verifyNoInteractions(tareaMapper);
                verify(tareaRepository, never()).save(any());
        }

        @Test
        void crearTarea_debeLanzarExcepcionCuandoResponsableEsNulo() {
                TareaRequestDTO request = TareaRequestDTO.builder()
                                .descripcion("Tarea sin responsable")
                                .proyectoId(1L)
                                .responsableId(null)
                                .estado(EstadoTarea.PENDING)
                                .build();

                when(proyectoClientFacade.existeProyecto(1L)).thenReturn(true);

                ReglaNegocioException exception = assertThrows(
                                ReglaNegocioException.class,
                                () -> tareaService.crearTarea(request));

                assertEquals("La tarea debe tener un responsable asignado", exception.getMessage());

                verify(proyectoClientFacade).existeProyecto(1L);
                verifyNoInteractions(tareaMapper);
                verify(tareaRepository, never()).save(any());
        }

        @Test
        void crearTarea_debeRechazarEstadoInicialDistintoDePending() {
                TareaRequestDTO request = TareaRequestDTO.builder()
                                .descripcion("Tarea con estado inicial inválido")
                                .proyectoId(1L)
                                .responsableId(10L)
                                .estado(EstadoTarea.IN_PROGRESS)
                                .build();

                when(proyectoClientFacade.existeProyecto(1L)).thenReturn(true);

                ReglaNegocioException exception = assertThrows(
                                ReglaNegocioException.class,
                                () -> tareaService.crearTarea(request));

                assertEquals("Una tarea nueva debe iniciar en estado PENDING", exception.getMessage());

                verify(proyectoClientFacade).existeProyecto(1L);
                verifyNoInteractions(tareaMapper);
                verify(tareaRepository, never()).save(any());
        }

        @Test
        void cambiarEstadoTarea_debePermitirTransicionDePendingAInProgress() {
                Long tareaId = 1L;

                Tarea tarea = Tarea.builder()
                                .id(tareaId)
                                .descripcion("Tarea pendiente")
                                .proyectoId(1L)
                                .responsableId(10L)
                                .estado(EstadoTarea.PENDING)
                                .build();

                CambiarEstadoTareaRequestDTO request = CambiarEstadoTareaRequestDTO.builder()
                                .estado(EstadoTarea.IN_PROGRESS)
                                .build();

                when(tareaRepository.findById(tareaId)).thenReturn(Optional.of(tarea));
                when(tareaRepository.save(any(Tarea.class))).thenAnswer(invocation -> invocation.getArgument(0));
                when(tareaMapper.toResponseDTO(any(Tarea.class))).thenAnswer(invocation -> {
                        Tarea tareaActualizada = invocation.getArgument(0);
                        return TareaResponseDTO.builder()
                                        .id(tareaActualizada.getId())
                                        .descripcion(tareaActualizada.getDescripcion())
                                        .proyectoId(tareaActualizada.getProyectoId())
                                        .responsableId(tareaActualizada.getResponsableId())
                                        .estado(tareaActualizada.getEstado())
                                        .build();
                });

                TareaResponseDTO response = tareaService.cambiarEstadoTarea(tareaId, request);

                assertEquals(EstadoTarea.IN_PROGRESS, response.getEstado());

                verify(tareaRepository).findById(tareaId);
                verify(tareaRepository).save(any(Tarea.class));
        }

        @Test
        void cambiarEstadoTarea_debePermitirTransicionDeInProgressADone() {
                Long tareaId = 2L;

                Tarea tarea = Tarea.builder()
                                .id(tareaId)
                                .descripcion("Tarea en progreso")
                                .proyectoId(1L)
                                .responsableId(10L)
                                .estado(EstadoTarea.IN_PROGRESS)
                                .build();

                CambiarEstadoTareaRequestDTO request = CambiarEstadoTareaRequestDTO.builder()
                                .estado(EstadoTarea.DONE)
                                .build();

                when(tareaRepository.findById(tareaId)).thenReturn(Optional.of(tarea));
                when(tareaRepository.save(any(Tarea.class))).thenAnswer(invocation -> invocation.getArgument(0));
                when(tareaMapper.toResponseDTO(any(Tarea.class))).thenAnswer(invocation -> {
                        Tarea tareaActualizada = invocation.getArgument(0);
                        return TareaResponseDTO.builder()
                                        .id(tareaActualizada.getId())
                                        .descripcion(tareaActualizada.getDescripcion())
                                        .proyectoId(tareaActualizada.getProyectoId())
                                        .responsableId(tareaActualizada.getResponsableId())
                                        .estado(tareaActualizada.getEstado())
                                        .build();
                });

                TareaResponseDTO response = tareaService.cambiarEstadoTarea(tareaId, request);

                assertEquals(EstadoTarea.DONE, response.getEstado());

                verify(tareaRepository).findById(tareaId);
                verify(tareaRepository).save(any(Tarea.class));
        }

        @Test
        void asignarResponsable_debeActualizarResponsableDeLaTarea() {
                Long tareaId = 1L;

                Tarea tarea = Tarea.builder()
                                .id(tareaId)
                                .descripcion("Asignar responsable")
                                .proyectoId(1L)
                                .responsableId(10L)
                                .estado(EstadoTarea.PENDING)
                                .build();

                AsignarResponsableRequestDTO request = AsignarResponsableRequestDTO.builder()
                                .responsableId(25L)
                                .build();

                when(tareaRepository.findById(tareaId)).thenReturn(Optional.of(tarea));
                when(tareaRepository.save(any(Tarea.class))).thenAnswer(invocation -> invocation.getArgument(0));
                when(tareaMapper.toResponseDTO(any(Tarea.class))).thenAnswer(invocation -> {
                        Tarea tareaActualizada = invocation.getArgument(0);
                        return TareaResponseDTO.builder()
                                        .id(tareaActualizada.getId())
                                        .descripcion(tareaActualizada.getDescripcion())
                                        .proyectoId(tareaActualizada.getProyectoId())
                                        .responsableId(tareaActualizada.getResponsableId())
                                        .estado(tareaActualizada.getEstado())
                                        .build();
                });

                TareaResponseDTO response = tareaService.asignarResponsable(tareaId, request);

                assertEquals(25L, response.getResponsableId());

                verify(tareaRepository).findById(tareaId);
                verify(tareaRepository).save(any(Tarea.class));
        }

        @Test
        void listarTareasPorEstado_debeRetornarTareasFiltradas() {
                Tarea tarea = Tarea.builder()
                                .id(1L)
                                .descripcion("Tarea pendiente")
                                .proyectoId(1L)
                                .responsableId(10L)
                                .estado(EstadoTarea.PENDING)
                                .build();

                TareaResponseDTO response = TareaResponseDTO.builder()
                                .id(1L)
                                .descripcion("Tarea pendiente")
                                .proyectoId(1L)
                                .responsableId(10L)
                                .estado(EstadoTarea.PENDING)
                                .build();

                when(tareaRepository.findByEstado(EstadoTarea.PENDING)).thenReturn(List.of(tarea));
                when(tareaMapper.toResponseDTO(tarea)).thenReturn(response);

                List<TareaResponseDTO> resultado = tareaService.listarTareasPorEstado(EstadoTarea.PENDING);

                assertEquals(1, resultado.size());
                assertEquals(EstadoTarea.PENDING, resultado.get(0).getEstado());

                verify(tareaRepository).findByEstado(EstadoTarea.PENDING);
        }

        @Test
        void listarTareasPorResponsable_debeRetornarTareasFiltradas() {
                Tarea tarea = Tarea.builder()
                                .id(1L)
                                .descripcion("Tarea por responsable")
                                .proyectoId(1L)
                                .responsableId(10L)
                                .estado(EstadoTarea.PENDING)
                                .build();

                TareaResponseDTO response = TareaResponseDTO.builder()
                                .id(1L)
                                .descripcion("Tarea por responsable")
                                .proyectoId(1L)
                                .responsableId(10L)
                                .estado(EstadoTarea.PENDING)
                                .build();

                when(tareaRepository.findByResponsableId(10L)).thenReturn(List.of(tarea));
                when(tareaMapper.toResponseDTO(tarea)).thenReturn(response);

                List<TareaResponseDTO> resultado = tareaService.listarTareasPorResponsable(10L);

                assertEquals(1, resultado.size());
                assertEquals(10L, resultado.get(0).getResponsableId());

                verify(tareaRepository).findByResponsableId(10L);
        }
}