package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepo extends JpaRepository<Inscripcion, String> {
}
