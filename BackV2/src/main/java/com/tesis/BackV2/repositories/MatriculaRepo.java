package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatriculaRepo extends JpaRepository<Matricula, Long> {

    // ultimo registro de una inscripcion
    Matricula findTopByInscripcionCedulaOrderByIdDesc(String cedula);
}
