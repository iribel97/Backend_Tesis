package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteRepo extends JpaRepository<Estudiante, String> {
}
