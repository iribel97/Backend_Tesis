package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Distributivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributivoRepo extends JpaRepository<Distributivo, Long> {

    // Traer por ciclo academico
    List<Distributivo> findByCicloId(long cicloId);
}
