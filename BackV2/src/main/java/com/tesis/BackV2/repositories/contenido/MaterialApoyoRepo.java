package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.MaterialApoyo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialApoyoRepo extends JpaRepository<MaterialApoyo, Long> {
}
