package com.innovatech.ms_equipos.repository;

import com.innovatech.ms_equipos.entity.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MiembroRepository extends JpaRepository<Miembro, Long> {

    Optional<Miembro> findByIdentificador(String identificador);

    boolean existsByIdentificador(String identificador);
}