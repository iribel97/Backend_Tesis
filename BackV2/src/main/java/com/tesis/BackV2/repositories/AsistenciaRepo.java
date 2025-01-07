package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaRepo extends JpaRepository<Asistencia, Long> {

    @Query("SELECT a FROM Asistencia a " +
            "WHERE a.estudiante.id = :estudianteId " +
            "AND a.horario.distributivo.id = :distributivoId")
    List<Asistencia> findByEstudianteAndDistributivo(@Param("estudianteId") Long estudianteId,
                                                     @Param("distributivoId") Long distributivoId);
}
