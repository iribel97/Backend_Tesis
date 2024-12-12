package com.tesis.BackV2.repositories.config;

import com.tesis.BackV2.entities.config.InscripcionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionConfigRepo extends JpaRepository<InscripcionConfig, Long> {
}
