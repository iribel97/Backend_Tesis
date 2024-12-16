package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregaRepo extends JpaRepository<Entrega, Long> {
}
