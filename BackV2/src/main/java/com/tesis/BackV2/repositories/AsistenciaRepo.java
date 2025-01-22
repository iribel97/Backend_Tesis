package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Asistencia;
import com.tesis.BackV2.enums.EstadoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AsistenciaRepo extends JpaRepository<Asistencia, Long> {

    @Query("SELECT a FROM Asistencia a " +
            "WHERE a.estudiante.id = :estudianteId " +
            "AND a.distributivo.id = :distributivoId")
    List<Asistencia> findByEstudianteAndDistributivo(@Param("estudianteId") Long estudianteId,
                                                     @Param("distributivoId") Long distributivoId);

    List<Asistencia> findByDistributivo_IdAndFecha(long horarioDistributivoId, LocalDate fecha);

    // Traer asistencias por ciclo academico
    List<Asistencia> findByCicloAcademico_Id(long cicloAcademicoId);

    // listar asistencia por curso
    List<Asistencia> findByDistributivo_Curso_Id(long cursoId);

    // quiero obtener el estudiante que tiene mas faltas de un curso
    @Query("SELECT a.estudiante.usuario.apellidos, a.estudiante.usuario.nombres , COUNT(a) as faltas " +
            "FROM Asistencia a " +
            "WHERE a.distributivo.curso.id = :cursoId " +
            "AND a.estado = :estado " +
            "GROUP BY a.estudiante " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> findEstudianteConMasFaltas(@Param("cursoId") Long cursoId,
                                              @Param("estado") EstadoAsistencia estado);

    // asistencia por distributivo
    List<Asistencia> findByDistributivo_Id(long distributivoId);
}
