package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemaRepo extends JpaRepository<Tema, Long> {
}
