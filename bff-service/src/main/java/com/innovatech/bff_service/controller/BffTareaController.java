package com.innovatech.bff_service.controller;

import com.innovatech.bff_service.dto.TareaDTO;
import com.innovatech.bff_service.dto.TareaRequestDTO;
import com.innovatech.bff_service.service.BffTareaService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/bff/tareas")
@RequiredArgsConstructor
@Tag(
        name = "BFF Tareas",
        description = "Endpoints de tareas adaptados para el frontend"
)
public class BffTareaController {

    private final BffTareaService bffTareaService;

    @Operation(summary = "Listar tareas de un proyecto para el tablero")
    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<TareaDTO>> listarTareasPorProyecto(
            @PathVariable
            @Positive(message = "El id del proyecto debe ser mayor a cero")
            Long proyectoId
    ) {
        List<TareaDTO> response = bffTareaService.listarTareasPorProyecto(proyectoId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear una tarea desde el frontend")
    @PostMapping
    public ResponseEntity<TareaDTO> crearTarea(
            @Valid @RequestBody TareaRequestDTO request
    ) {
        TareaDTO response = bffTareaService.crearTarea(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
