package com.innovatech.ms_proyectos.controller;

import com.innovatech.ms_proyectos.dto.CambiarEstadoProyectoRequestDTO;
import com.innovatech.ms_proyectos.dto.ProyectoExisteResponseDTO;
import com.innovatech.ms_proyectos.dto.ProyectoRequestDTO;
import com.innovatech.ms_proyectos.dto.ProyectoResponseDTO;
import com.innovatech.ms_proyectos.service.ProyectoService;
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
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
@Tag(
        name = "Gestión de Proyectos",
        description = "Endpoints para administrar proyectos tecnológicos"
)
public class ProyectoController {

    private final ProyectoService proyectoService;

    @Operation(summary = "Registrar un nuevo proyecto")
    @PostMapping
    public ResponseEntity<ProyectoResponseDTO> registrarProyecto(
            @Valid @RequestBody ProyectoRequestDTO request
    ) {
        ProyectoResponseDTO response = proyectoService.registrarProyecto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar proyectos")
    @GetMapping
    public ResponseEntity<List<ProyectoResponseDTO>> listarProyectos() {
        List<ProyectoResponseDTO> response = proyectoService.listarProyectos();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener proyecto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponseDTO> obtenerProyectoPorId(
            @Parameter(description = "ID interno del proyecto", example = "1")
            @PathVariable
            @Positive(message = "El id del proyecto debe ser mayor a cero")
            Long id
    ) {
        ProyectoResponseDTO response = proyectoService.obtenerProyectoPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar proyecto")
    @PutMapping("/{id}")
    public ResponseEntity<ProyectoResponseDTO> actualizarProyecto(
            @Parameter(description = "ID interno del proyecto", example = "1")
            @PathVariable
            @Positive(message = "El id del proyecto debe ser mayor a cero")
            Long id,

            @Valid @RequestBody ProyectoRequestDTO request
    ) {
        ProyectoResponseDTO response = proyectoService.actualizarProyecto(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar estado de un proyecto")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ProyectoResponseDTO> cambiarEstadoProyecto(
            @Parameter(description = "ID interno del proyecto", example = "1")
            @PathVariable
            @Positive(message = "El id del proyecto debe ser mayor a cero")
            Long id,

            @Valid @RequestBody CambiarEstadoProyectoRequestDTO request
    ) {
        ProyectoResponseDTO response = proyectoService.cambiarEstadoProyecto(id, request.getEstado());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validar si un proyecto existe")
    @GetMapping("/{id}/existe")
    public ResponseEntity<ProyectoExisteResponseDTO> validarExistenciaProyecto(
            @Parameter(description = "ID interno del proyecto", example = "1")
            @PathVariable
            @Positive(message = "El id del proyecto debe ser mayor a cero")
            Long id
    ) {
        ProyectoExisteResponseDTO response = proyectoService.validarExistenciaProyecto(id);
        return ResponseEntity.ok(response);
    }
}