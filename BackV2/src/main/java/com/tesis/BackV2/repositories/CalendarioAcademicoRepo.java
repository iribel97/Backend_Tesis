package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.CalendarioAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarioAcademicoRepo extends JpaRepository<CalendarioAcademico, Long> {

    List<CalendarioAcademico> findAllByCicloId(Long cicloId);

    CalendarioAcademico findByDescripcion(String descripcion);

    boolean existsByDescripcion(String descripcion);
}
