package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Citacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitacionRepo extends JpaRepository<Citacion, Long> {

    // Traer citaciones por id del docente
    List<Citacion> findByDocenteIdAndConfirmadaFalse(long id);
}
