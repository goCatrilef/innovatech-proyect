package com.innovatech.bff_service.controller;

import com.innovatech.bff_service.dto.AsignarMiembroProyectoRequestDTO;
import com.innovatech.bff_service.dto.MiembroDTO;
import com.innovatech.bff_service.dto.MiembroProyectoDTO;
import com.innovatech.bff_service.dto.MiembroRequestDTO;
import com.innovatech.bff_service.service.BffEquipoService;
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
@RequestMapping("/api/bff/equipos")
@RequiredArgsConstructor
@Tag(
        name = "BFF Equipos",
        description = "Endpoints de equipo adaptados para el frontend"
)
public class BffEquipoController {

    private final BffEquipoService bffEquipoService;

    @Operation(summary = "Listar miembros del equipo")
    @GetMapping("/miembros")
    public ResponseEntity<List<MiembroDTO>> listarMiembros() {
        return ResponseEntity.ok(bffEquipoService.listarMiembros());
    }

    @Operation(summary = "Registrar miembro desde el frontend")
    @PostMapping("/miembros")
    public ResponseEntity<MiembroDTO> registrarMiembro(
            @Valid @RequestBody MiembroRequestDTO request
    ) {
        MiembroDTO response = bffEquipoService.registrarMiembro(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Asociar miembro a proyecto")
    @PostMapping("/asignaciones")
    public ResponseEntity<MiembroProyectoDTO> asignarMiembroAProyecto(
            @Valid @RequestBody AsignarMiembroProyectoRequestDTO request
    ) {
        MiembroProyectoDTO response = bffEquipoService.asignarMiembroAProyecto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar miembros asignados a un proyecto")
    @GetMapping("/proyectos/{proyectoId}/miembros")
    public ResponseEntity<List<MiembroProyectoDTO>> listarMiembrosPorProyecto(
            @PathVariable
            @Positive(message = "El id del proyecto debe ser mayor a cero")
            Long proyectoId
    ) {
        return ResponseEntity.ok(bffEquipoService.listarMiembrosPorProyecto(proyectoId));
    }
}
