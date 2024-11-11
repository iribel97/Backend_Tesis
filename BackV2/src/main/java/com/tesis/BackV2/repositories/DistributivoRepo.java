package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Distributivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributivoRepo extends JpaRepository<Distributivo, Long> {
}
