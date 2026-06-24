package com.innovatech.ms_proyectos.repository;

import com.innovatech.ms_proyectos.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    Optional<Proyecto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}