package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Grado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradoRepo extends JpaRepository<Grado, Long> {

    // Buscar por nombre
    Grado findByNombre(String nombre);
}
