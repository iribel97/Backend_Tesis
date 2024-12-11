package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromocionRepo extends JpaRepository<Promocion, Long> {
}
