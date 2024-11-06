package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Representante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepresentanteRepo extends JpaRepository<Representante, Long> {

    Representante findByUsuarioCedula(String cedula);
}
