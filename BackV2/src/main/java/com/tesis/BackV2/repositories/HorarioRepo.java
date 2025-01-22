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

    // Listar horatio por id del docente
    List<Horario> findByDistributivoDocenteId(Long id);

    // Listar horarios que coincida con el día de la semana y el id del docente
    List<Horario> findByDiaSemanaAndDistributivoDocenteId(DiaSemana diaSemana, Long id);

    // traer por docente
    List<Horario> findByDistributivoDocenteUsuarioCedula(String cedula);
}
