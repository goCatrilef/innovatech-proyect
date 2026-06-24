package com.innovatech.ms_equipos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "miembro_proyecto",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_miembro_proyecto",
                        columnNames = {"miembro_id", "proyecto_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "miembro_id", nullable = false)
    private Miembro miembro;

    @Column(name = "proyecto_id", nullable = false)
    private Long proyectoId;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;

    @PrePersist
    public void prePersist() {
        this.fechaAsignacion = LocalDateTime.now();
    }
}