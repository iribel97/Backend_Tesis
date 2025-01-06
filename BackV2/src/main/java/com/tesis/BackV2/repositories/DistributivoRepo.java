package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Distributivo;
import org.springframework.data.jpa.repository.JpaRepository;
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

    // Traer distributivo por materia, curso
    Distributivo findByCursoIdAndMateriaId(long cursoId, long materiaId);

    // Existe por id del ciclo
    boolean existsByCicloId(long cicloId);

    boolean existsByCursoId(Long id);

    boolean existsByMateriaId(Long id);

    boolean existsByCursoIdAndMateriaId(Long aulaId, Long materiaId);

    boolean existsByDocenteId(long id);

    Distributivo findByIdAndDocente_Usuario_Cedula(Long idDistributivo, String cedulaDocente);

    Collection<Distributivo> findByCicloIdAndCursoId(Long cicloId, Long cursoId);

    Collection<Distributivo> findByCicloIdAndDocente_Usuario_Cedula(Long cicloId, String cedula);
}
