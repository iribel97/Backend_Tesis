package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionRepo extends JpaRepository<Asignacion, Long> {
}
