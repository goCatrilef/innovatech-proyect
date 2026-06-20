package com.innovatech.ms_equipos.repository;

import com.innovatech.ms_equipos.entity.MiembroProyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MiembroProyectoRepository extends JpaRepository<MiembroProyecto, Long> {

    boolean existsByMiembroIdAndProyectoId(Long miembroId, Long proyectoId);

    List<MiembroProyecto> findByProyectoId(Long proyectoId);

    List<MiembroProyecto> findByMiembroId(Long miembroId);
}
