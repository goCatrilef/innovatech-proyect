package com.innovatech.bff_service.controller;

import com.innovatech.bff_service.dto.ProyectoResumenDTO;
import com.innovatech.bff_service.service.BffProyectoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/bff/proyectos")
@RequiredArgsConstructor
@Tag(
        name = "BFF Proyectos",
        description = "Endpoints compuestos para el frontend"
)
public class BffProyectoController {

    private final BffProyectoService bffProyectoService;

    @Operation(summary = "Obtener resumen consolidado de un proyecto")
    @GetMapping("/{proyectoId}/resumen")
    public ResponseEntity<ProyectoResumenDTO> obtenerResumenProyecto(
            @PathVariable
            @Positive(message = "El id del proyecto debe ser mayor a cero")
            Long proyectoId
    ) {
        ProyectoResumenDTO response = bffProyectoService.obtenerResumenProyecto(proyectoId);
        return ResponseEntity.ok(response);
    }
}