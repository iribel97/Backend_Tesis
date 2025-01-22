package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Distributivo;
import com.tesis.BackV2.entities.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DistributivoRepo extends JpaRepository<Distributivo, Long> {

    // Traer por ciclo academico
    List<Distributivo> findByCicloId(long cicloId);

    // Traer por curso
    List<Distributivo> findByCursoId(long cursoId);

    List<Distributivo> findByDocente_Usuario_Cedula(String docenteUsuarioCedula);

    // traer por id de docente
    List<Distributivo> findByDocenteId(long id);

    // Traer distributivo por materia, curso
    Distributivo findByCursoIdAndMateriaId(long cursoId, long materiaId);

    // Existe por id del ciclo
    boolean existsByCicloId(long cicloId);

    boolean existsByCursoId(Long id);

    boolean existsByMateriaId(Long id);

    boolean existsByCursoIdAndMateriaIdAndCicloId(Long cursoId, Long materiaId, Long cicloId);

    boolean existsByDocenteId(long id);

    Distributivo findByIdAndDocente_Usuario_Cedula(Long idDistributivo, String cedulaDocente);

    Collection<Distributivo> findByCicloIdAndCursoId(Long cicloId, Long cursoId);

    Collection<Distributivo> findByCicloIdAndDocente_Usuario_Cedula(Long cicloId, String cedula);

    @Query("SELECT DISTINCT d.docente FROM Distributivo d WHERE d.ciclo.id = :cicloId")
    List<Docente> findDistinctDocentesByCicloId(@Param("cicloId") long cicloId);

}
