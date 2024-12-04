package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.HorarioDTO;
import com.tesis.BackV2.entities.Distributivo;
import com.tesis.BackV2.entities.Horario;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.DistributivoRepo;
import com.tesis.BackV2.repositories.HorarioRepo;
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
    public ApiResponse<String> crearHorario(HorarioRequest request) {
        // Traer el distributivo
        Distributivo distributivo = distributivoRepo.findById(request.getIdDistributivo())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El distributivo no ha sido encontrado")
                        .build()
                ));

        if (distributivo.getCurso() == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El distributivo no tiene un curso asignado")
                    .build()
            );
        }

        // Verificar si el horario ya existe o se cruza con otro horario
        boolean horarioCruzado = horarioRepo.findByDiaSemanaAndDistributivoCursoId(request.getDiaSemana(), distributivo.getCurso().getId())
                .stream()
                .anyMatch(horarioExistente ->
                            (request.getHoraInicio().isBefore(horarioExistente.getHoraFin()) &&
                             request.getHoraFin().isAfter(horarioExistente.getHoraInicio())));

        if (horarioCruzado) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("Existe un choque de horarios")
                    .build()
            );
        }

        if(validarDistributivoHoras(request, 0)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El docente ya tiene un horario asignado en ese día y hora")
                    .build()
            );
        }


        // Obtener la cantidad de horas
        int cantHoras = calcularHoras(request.getHoraInicio(), request.getHoraFin());

        // Traer las horas de la materia
        int horasMateria = distributivo.getMateria().getHoras();

        // Validar la cantidad de horas asignadas
        if (cantHoras <= 0) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas no puede ser menor o igual a 0")
                    .build()
            );
        }
        if (distributivo.getHorasAsignadas() == horasMateria) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas asignadas ya es igual a las horas de la materia")
                    .build()
            );
        }
        if (distributivo.getHorasAsignadas() + cantHoras > horasMateria) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas asignadas supera las horas de la materia")
                    .build()
            );
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
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Horario creado")
                .codigo(200)
                .detalles("El horario ha sido creado correctamente")
                .build();
    }

    // Actualizar
    @Transactional
    public ApiResponse<String> editarHorario(HorarioRequest request) {
        // Traer el horario
        Horario horario = horarioRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El horario no ha sido encontrado")
                        .build()
                ));

        // Traer el distributivo
        Distributivo distributivo = distributivoRepo.findById(request.getIdDistributivo())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El distributivo no encontrado")
                        .build()
                ));

        if (distributivo.getCurso() == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El distributivo no tiene un curso asignado")
                    .build()
            );
        }

        // Verificar si el horario ya existe o se cruza con otro horario diferente al actual
        boolean horarioCruzado = horarioRepo.findByDiaSemanaAndDistributivoCursoId(request.getDiaSemana(), distributivo.getCurso().getId())
                .stream()
                .anyMatch(horarioExistente ->
                        (request.getHoraInicio().isBefore(horarioExistente.getHoraFin()) &&
                                request.getHoraFin().isAfter(horarioExistente.getHoraInicio())) &&
                                horarioExistente.getId() != request.getId());

        if (horarioCruzado) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("Existe un choque de horarios")
                    .build()
            );
        }

        if(validarDistributivoHoras(request, request.getId())) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El docente ya tiene un horario asignado en ese día y hora")
                    .build()
            );
        }

        // Obtener la cantidad de horas
        int cantHoras = calcularHoras(request.getHoraInicio(), request.getHoraFin());

        // Traer las horas de la materia
        int horasMateria = distributivo.getMateria().getHoras();

        // Validar la cantidad de horas asignadas
        if (cantHoras <= 0) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas no puede ser menor o igual a 0")
                    .build()
            );
        }

        if (distributivo.getHorasAsignadas() - horario.getCantHoras() + cantHoras >= horasMateria) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas asignadas ya es igual a las horas de la materia")
                    .build()
            );
        }

        // Actualizar las horas asignadas
        distributivo.setHorasAsignadas(distributivo.getHorasAsignadas() - horario.getCantHoras() + cantHoras);
        distributivoRepo.save(distributivo);

        // Actualizar el horario
        horario.setHoraInicio(request.getHoraInicio());
        horario.setHoraFin(request.getHoraFin());
        horario.setCantHoras(cantHoras);
        horario.setDiaSemana(request.getDiaSemana());
        horario.setDistributivo(distributivo);

        horarioRepo.save(horario);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Horario actualizado")
                .codigo(200)
                .detalles("El horario ha sido actualizado correctamente")
                .build();



    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarHorario(long id) {
        // Traer el horario
        Horario horario = horarioRepo.findById(id)
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El horario no ha sido encontrado")
                        .build()
                ));

        // Traer el distributivo
        Distributivo distributivo = horario.getDistributivo();

        // Actualizar las horas asignadas
        distributivo.setHorasAsignadas(distributivo.getHorasAsignadas() - horario.getCantHoras());
        distributivoRepo.save(distributivo);

        // Eliminar el horario
        horarioRepo.delete(horario);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Horario eliminado")
                .codigo(200)
                .detalles("El horario ha sido eliminado correctamente")
                .build();
    }

    // Traer horarios por curso
    public List<HorarioDTO> getHorariosByCurso(long id) {
        return horarioRepo.findByDistributivoCursoId(id)
                .stream()
                .map(horario -> HorarioDTO.builder()
                        .id(horario.getId())
                        .diaSemana(horario.getDiaSemana().name())
                        .horaInicio(horario.getHoraInicio().toString())
                        .horaFin(horario.getHoraFin().toString())
                        .ciclo(horario.getDistributivo().getCiclo().getNombre())
                        .curso(horario.getDistributivo().getCurso().getGrado().getNombre() + " " + horario.getDistributivo().getCurso().getParalelo())
                        .materia(horario.getDistributivo().getMateria().getNombre())
                        .docente(horario.getDistributivo().getDocente().getUsuario().getNombres() + " " + horario.getDistributivo().getDocente().getUsuario().getApellidos())
                        .build()
                )
                .toList();
    }

    private int calcularHoras(LocalTime horaInicio, LocalTime horaFin) {
        if (horaFin.isBefore(horaInicio)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La hora de fin no puede ser antes de la hora de inicio")
                    .build()
            );
        }
        Duration duracion = Duration.between(horaInicio, horaFin);
        return (int) duracion.toHours(); // Convertir la duración a horas enteras
    }

    private boolean validarDistributivoHoras(HorarioRequest request, long idHorario) {
        // traer distributivos
        List<Horario> horarios = horarioRepo.findAll();
        // traer distributivo del request
        Distributivo distributivo = distributivoRepo.findById(request.getIdDistributivo()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(404)
                .detalles("El distributivo no ha sido encontrado")
                .build()
        ));

        for (Horario horario : horarios) {
            if(horario.getDistributivo().getDocente() == distributivo.getDocente()
                    && horario.getDiaSemana().equals(request.getDiaSemana())
                    && (request.getHoraInicio().isBefore(horario.getHoraFin())
                    && request.getHoraFin().isAfter(horario.getHoraInicio()))
                    && horario.getDistributivo().getMateria() == distributivo.getMateria()
                    && horario.getDistributivo().getCiclo() == distributivo.getCiclo()
                    && horario.getId() != idHorario) {

                return true;

            }
        }

        return false;
    }

}
