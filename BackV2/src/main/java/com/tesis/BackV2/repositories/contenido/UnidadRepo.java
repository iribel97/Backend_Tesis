package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadRepo extends JpaRepository<Unidad, Long> {
}
