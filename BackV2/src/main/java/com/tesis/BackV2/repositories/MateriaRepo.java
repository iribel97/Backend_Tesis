package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MateriaRepo extends JpaRepository<Materia, Long> {

    // Existe por nombre
    boolean existsByNombre(String nombre);

    // Existe por grado
    boolean existsByGradoNombre(String grado);
}
