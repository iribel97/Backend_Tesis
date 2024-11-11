package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AulaRepo extends JpaRepository<Aula, Long> {

    // Existe por paralelo
    boolean existsByParalelo(String paralelo);

    // Existe por grado
    boolean existsByGradoId(long gradoId);

    // Existe por tutor
    boolean existsByTutorId(long tutorId);
}
