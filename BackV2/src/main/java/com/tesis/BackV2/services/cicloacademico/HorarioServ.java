package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.entities.Distributivo;
import com.tesis.BackV2.entities.Horario;
import com.tesis.BackV2.entities.Materia;
import com.tesis.BackV2.enums.DiaSemana;
import com.tesis.BackV2.repositories.DistributivoRepo;
import com.tesis.BackV2.repositories.HorarioRepo;
import com.tesis.BackV2.repositories.MateriaRepo;
import com.tesis.BackV2.request.HorarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
public class HorarioServ {
    @Autowired
    private HorarioRepo horarioRepo;
    @Autowired
    private DistributivoRepo distributivoRepo;

    // Crear
    @Transactional
    public String crearHorario(HorarioRequest request) {
        // Traer el distributivo
        Distributivo distributivo = distributivoRepo.findById(request.getIdDistributivo())
                .orElseThrow(() -> new RuntimeException("Distributivo no encontrado"));

        // Verificar si el horario ya existe o se cruza con otro horario
        boolean horarioCruzado = horarioRepo.findByDiaSemanaAndDistributivoAulaId(request.getDiaSemana(), distributivo.getAula().getId())
                .stream()
                .anyMatch(horarioExistente ->
                            (request.getHoraInicio().isBefore(horarioExistente.getHoraFin()) &&
                             request.getHoraFin().isAfter(horarioExistente.getHoraInicio())));

        if (horarioCruzado) {
            throw new RuntimeException("El horario se cruza con otro existente");
        }

        if(validarDistributivoHoras(request)) {
            throw new RuntimeException("El docente ya tiene un horario asignado en ese día y hora");
        }


        // Obtener la cantidad de horas
        int cantHoras = calcularHoras(request.getHoraInicio(), request.getHoraFin());

        // Traer las horas de la materia
        int horasMateria = distributivo.getMateria().getHoras();

        // Validar la cantidad de horas asignadas
        if (cantHoras <= 0) {
            throw new RuntimeException("La cantidad de horas debe ser mayor a 0");
        }
        if (distributivo.getHorasAsignadas() >= horasMateria) {
            throw new RuntimeException("Ya se asignaron todas las horas de la materia");
        }
        if (distributivo.getHorasAsignadas() + cantHoras > horasMateria) {
            throw new RuntimeException("La cantidad de horas asignadas supera las horas de la materia");
        }

        // Actualizar las horas asignadas
        distributivo.setHorasAsignadas(distributivo.getHorasAsignadas() + cantHoras);
        distributivoRepo.save(distributivo);

        // Crear y guardar el horario
        Horario horario = Horario.builder()
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .cantHoras(cantHoras)
                .diaSemana(request.getDiaSemana())
                .distributivo(distributivo)
                .build();

        horarioRepo.save(horario);
        return "Horario creado";
    }


    public int calcularHoras(LocalTime horaInicio, LocalTime horaFin) {
        if (horaFin.isBefore(horaInicio)) {
            throw new RuntimeException("La hora de fin no puede ser antes de la hora de inicio");
        }
        Duration duracion = Duration.between(horaInicio, horaFin);
        return (int) duracion.toHours(); // Convertir la duración a horas enteras
    }

    private boolean validarDistributivoHoras(HorarioRequest request) {
        // traer distributivos
        List<Horario> horarios = horarioRepo.findAll();
        // traer distributivo del request
        Distributivo distributivo = distributivoRepo.findById(request.getIdDistributivo()).orElseThrow(() -> new RuntimeException("Distributivo no encontrado"));

        for (Horario horario : horarios) {
            if(horario.getDistributivo().getDocente() == distributivo.getDocente()
                    && horario.getDiaSemana().equals(request.getDiaSemana())
                    && (request.getHoraInicio().isBefore(horario.getHoraFin())
                    && request.getHoraFin().isAfter(horario.getHoraInicio()))
                    && horario.getDistributivo().getMateria() == distributivo.getMateria()) {

                return true;

            }
        }

        return false;
    }

}
