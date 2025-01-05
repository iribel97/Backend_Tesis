package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsistenciaRepo extends JpaRepository<Asistencia, Long> {
}
