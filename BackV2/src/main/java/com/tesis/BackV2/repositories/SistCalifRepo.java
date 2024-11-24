package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.SistemaCalificacion;
import com.tesis.BackV2.entities.embedded.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SistCalifRepo extends JpaRepository<SistemaCalificacion, Calificacion> {

    @Query("SELECT COUNT(DISTINCT s.id.registro) FROM SistemaCalificacion s WHERE s.ciclo.id = :cicloId")
    long countSistemasByCicloId(@Param("cicloId") Long cicloId);
}
