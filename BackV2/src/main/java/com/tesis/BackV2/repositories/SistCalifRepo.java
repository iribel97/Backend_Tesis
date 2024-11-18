package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.SistemaCalificacion;
import com.tesis.BackV2.entities.embedded.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SistCalifRepo extends JpaRepository<SistemaCalificacion, Calificacion> {
}
