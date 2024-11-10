package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.CicloAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CicloAcademicoRepo extends JpaRepository<CicloAcademico, Integer> {
}
