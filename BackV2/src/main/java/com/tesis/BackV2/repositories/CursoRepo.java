package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepo extends JpaRepository<Curso, Long> {

    // Existe por paralelo
    boolean existsByParalelo(String paralelo);

    // Existe por grado
    boolean existsByGradoId(long gradoId);

    // Existe por tutor
    boolean existsByTutorId(long tutorId);

    // Traer por paralelo y nombre del grado
    Curso findByParaleloAndGradoNombre(String paralelo, String nombre);

    boolean existsByParaleloAndGradoNombre(String paralelo, String grado);
}
