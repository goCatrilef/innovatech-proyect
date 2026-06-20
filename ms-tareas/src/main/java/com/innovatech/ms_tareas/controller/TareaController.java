package com.innovatech.ms_tareas.controller;

import com.innovatech.ms_tareas.dto.AsignarResponsableRequestDTO;
import com.innovatech.ms_tareas.dto.CambiarEstadoTareaRequestDTO;
import com.innovatech.ms_tareas.dto.TareaRequestDTO;
import com.innovatech.ms_tareas.dto.TareaResponseDTO;
import com.innovatech.ms_tareas.entity.enums.EstadoTarea;
import com.innovatech.ms_tareas.service.TareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/tareas")
@RequiredArgsConstructor
@Tag(
        name = "Gestión de Tareas",
        description = "Endpoints para administrar tareas asociadas a proyectos"
)
public class TareaController {

    private final TareaService tareaService;

    @Operation(summary = "Crear una nueva tarea asociada a un proyecto")
    @PostMapping
    public ResponseEntity<TareaResponseDTO> crearTarea(
            @Valid @RequestBody TareaRequestDTO request
    ) {
        TareaResponseDTO response = tareaService.crearTarea(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener tarea por ID")
    @GetMapping("/{id}")
    public ResponseEntity<TareaResponseDTO> obtenerTareaPorId(
            @Parameter(description = "ID interno de la tarea", example = "1")
            @PathVariable
            @Positive(message = "El id de la tarea debe ser mayor a cero")
            Long id
    ) {
        TareaResponseDTO response = tareaService.obtenerTareaPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar tareas asociadas a un proyecto")
    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<TareaResponseDTO>> listarTareasPorProyecto(
            @Parameter(description = "ID del proyecto", example = "1")
            @PathVariable
            @Positive(message = "El id del proyecto debe ser mayor a cero")
            Long proyectoId
    ) {
        List<TareaResponseDTO> response = tareaService.listarTareasPorProyecto(proyectoId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar tareas por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TareaResponseDTO>> listarTareasPorEstado(
            @Parameter(description = "Estado de la tarea", example = "PENDING")
            @PathVariable
            EstadoTarea estado
    ) {
        List<TareaResponseDTO> response = tareaService.listarTareasPorEstado(estado);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar tareas por responsable")
    @GetMapping("/responsable/{responsableId}")
    public ResponseEntity<List<TareaResponseDTO>> listarTareasPorResponsable(
            @Parameter(description = "ID del responsable", example = "1")
            @PathVariable
            @Positive(message = "El id del responsable debe ser mayor a cero")
            Long responsableId
    ) {
        List<TareaResponseDTO> response = tareaService.listarTareasPorResponsable(responsableId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar información de una tarea")
    @PutMapping("/{id}")
    public ResponseEntity<TareaResponseDTO> actualizarTarea(
            @Parameter(description = "ID interno de la tarea", example = "1")
            @PathVariable
            @Positive(message = "El id de la tarea debe ser mayor a cero")
            Long id,

            @Valid @RequestBody TareaRequestDTO request
    ) {
        TareaResponseDTO response = tareaService.actualizarTarea(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar estado de una tarea")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<TareaResponseDTO> cambiarEstadoTarea(
            @Parameter(description = "ID interno de la tarea", example = "1")
            @PathVariable
            @Positive(message = "El id de la tarea debe ser mayor a cero")
            Long id,

            @Valid @RequestBody CambiarEstadoTareaRequestDTO request
    ) {
        TareaResponseDTO response = tareaService.cambiarEstadoTarea(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Asignar responsable a una tarea")
    @PatchMapping("/{id}/responsable")
    public ResponseEntity<TareaResponseDTO> asignarResponsable(
            @Parameter(description = "ID interno de la tarea", example = "1")
            @PathVariable
            @Positive(message = "El id de la tarea debe ser mayor a cero")
            Long id,

            @Valid @RequestBody AsignarResponsableRequestDTO request
    ) {
        TareaResponseDTO response = tareaService.asignarResponsable(id, request);
        return ResponseEntity.ok(response);
    }
}
