package com.innovatech.ms_equipos.controller;

import com.innovatech.ms_equipos.dto.AsignarMiembroProyectoRequestDTO;
import com.innovatech.ms_equipos.dto.MiembroProyectoResponseDTO;
import com.innovatech.ms_equipos.dto.MiembroRequestDTO;
import com.innovatech.ms_equipos.dto.MiembroResponseDTO;
import com.innovatech.ms_equipos.service.MiembroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
@Tag(
        name = "Gestión de Equipos",
        description = "Endpoints para registrar miembros, consultarlos y asociarlos a proyectos"
)
public class MiembroController {

    private final MiembroService miembroService;

    @Operation(
            summary = "Registrar un nuevo miembro",
            description = "Registra un miembro del equipo validando que el identificador sea único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Miembro registrado correctamente",
                    content = @Content(schema = @Schema(implementation = MiembroResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un miembro con el mismo identificador",
                    content = @Content
            )
    })
    @PostMapping("/miembros")
    public ResponseEntity<MiembroResponseDTO> registrarMiembro(
            @Valid @RequestBody MiembroRequestDTO request
    ) {
        MiembroResponseDTO response = miembroService.registrarMiembro(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Listar miembros",
            description = "Obtiene el listado completo de miembros registrados en el microservicio."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado obtenido correctamente"
            )
    })
    @GetMapping("/miembros")
    public ResponseEntity<List<MiembroResponseDTO>> listarMiembros() {
        List<MiembroResponseDTO> response = miembroService.listarMiembros();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtener miembro por ID",
            description = "Consulta la información de un miembro específico mediante su identificador interno."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Miembro encontrado",
                    content = @Content(schema = @Schema(implementation = MiembroResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Miembro no encontrado",
                    content = @Content
            )
    })
    @GetMapping("/miembros/{id}")
    public ResponseEntity<MiembroResponseDTO> obtenerMiembroPorId(
            @Parameter(description = "ID interno del miembro", example = "1")
            @PathVariable
            @Positive(message = "El id del miembro debe ser mayor a cero")
            Long id
    ) {
        MiembroResponseDTO response = miembroService.obtenerMiembroPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar miembro",
            description = "Actualiza los datos de un miembro existente validando que no se duplique el identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Miembro actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = MiembroResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Miembro no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto por identificador duplicado",
                    content = @Content
            )
    })
    @PutMapping("/miembros/{id}")
    public ResponseEntity<MiembroResponseDTO> actualizarMiembro(
            @Parameter(description = "ID interno del miembro", example = "1")
            @PathVariable
            @Positive(message = "El id del miembro debe ser mayor a cero")
            Long id,

            @Valid @RequestBody MiembroRequestDTO request
    ) {
        MiembroResponseDTO response = miembroService.actualizarMiembro(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Desactivar miembro",
            description = "Realiza una baja lógica del miembro, cambiando su estado activo a false."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Miembro desactivado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Miembro no encontrado",
                    content = @Content
            )
    })
    @PatchMapping("/miembros/{id}/desactivar")
    public ResponseEntity<Void> desactivarMiembro(
            @Parameter(description = "ID interno del miembro", example = "1")
            @PathVariable
            @Positive(message = "El id del miembro debe ser mayor a cero")
            Long id
    ) {
        miembroService.desactivarMiembro(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Asignar miembro a proyecto",
            description = "Asocia un miembro existente a un proyecto mediante el ID del proyecto."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Miembro asignado correctamente al proyecto",
                    content = @Content(schema = @Schema(implementation = MiembroProyectoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Miembro o proyecto no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El miembro ya está asignado al proyecto",
                    content = @Content
            )
    })
    @PostMapping("/asignaciones")
    public ResponseEntity<MiembroProyectoResponseDTO> asignarMiembroAProyecto(
            @Valid @RequestBody AsignarMiembroProyectoRequestDTO request
    ) {
        MiembroProyectoResponseDTO response = miembroService.asignarMiembroAProyecto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Listar miembros por proyecto",
            description = "Obtiene todos los miembros asignados a un proyecto específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de miembros asignados al proyecto"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID del proyecto inválido",
                    content = @Content
            )
    })
    @GetMapping("/proyectos/{proyectoId}/miembros")
    public ResponseEntity<List<MiembroProyectoResponseDTO>> listarMiembrosPorProyecto(
            @Parameter(description = "ID del proyecto", example = "1")
            @PathVariable
            @Positive(message = "El id del proyecto debe ser mayor a cero")
            Long proyectoId
    ) {
        List<MiembroProyectoResponseDTO> response = miembroService.listarMiembrosPorProyecto(proyectoId);
        return ResponseEntity.ok(response);
    }
}