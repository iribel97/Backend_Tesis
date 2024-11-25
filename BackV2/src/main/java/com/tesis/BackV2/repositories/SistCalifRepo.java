package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.SistemaCalificacion;
import com.tesis.BackV2.entities.embedded.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SistCalifRepo extends JpaRepository<SistemaCalificacion, Calificacion> {

    @Query("SELECT COUNT(DISTINCT s.id.registro) FROM SistemaCalificacion s WHERE s.ciclo.id = :cicloId")
    long countSistemasByCicloId(@Param("cicloId") Long cicloId);

    @Query("SELECT s FROM SistemaCalificacion s WHERE s.id.registro = :registro")
    List<SistemaCalificacion> porRegistro(@Param("registro") long registro);
}
