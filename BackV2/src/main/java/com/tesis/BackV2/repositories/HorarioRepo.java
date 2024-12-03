package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Horario;
import com.tesis.BackV2.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioRepo extends JpaRepository<Horario, Long> {
    boolean existsByHoraInicioAndHoraFinAndDistributivoId(LocalTime horaInicio, LocalTime horaFin, long id);

    boolean existsByHoraInicioAndHoraFinAndDiaSemanaAndDistributivoId(LocalTime horaInicio, LocalTime horaFin, String diaSemana, long id);

    List<Horario> findByDiaSemanaAndDistributivoCursoId(DiaSemana diaSemana, long id);

    // Taer horarios por aula
    List<Horario> findByDistributivoCursoId(long id);

}
