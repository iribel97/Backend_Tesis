package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.CicloAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CicloAcademicoRepo extends JpaRepository<CicloAcademico, Long> {

    // Devolver el último registro
    CicloAcademico findTopByOrderByIdDesc();

    // Devolver el ciclo académico que tenga "activo" en true
    CicloAcademico findByActivoTrue();
}
