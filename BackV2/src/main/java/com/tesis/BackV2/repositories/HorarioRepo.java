package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Horario;
import com.tesis.BackV2.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepo extends JpaRepository<Horario, Long> {

    List<Horario> findByDiaSemanaAndDistributivoCursoId(DiaSemana diaSemana, long id);

    // Taer horarios por aula
    List<Horario> findByDistributivoCursoId(long id);

    // Traer horarios por id del distributivo
    List<Horario> findByDistributivo_Id(long id);

    List<Horario> findByDistributivoId(Long id);

    // traer por docente
    List<Horario> findByDistributivoDocenteUsuarioCedula(String cedula);
}
