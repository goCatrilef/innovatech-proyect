package com.innovatech.ms_equipos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "miembros",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_miembro_identificador", columnNames = "identificador")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Miembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identificador", nullable = false, unique = true, length = 50)
    private String identificador;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "rol", nullable = false, length = 80)
    private String rol;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();

        if (this.activo == null) {
            this.activo = true;
        }
    }
}