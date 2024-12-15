package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.HorarioDTO;
import com.tesis.BackV2.entities.Distributivo;
import com.tesis.BackV2.entities.Horario;
import com.tesis.BackV2.entities.config.HorarioConfig;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.DistributivoRepo;
import com.tesis.BackV2.repositories.HorarioRepo;
import com.tesis.BackV2.repositories.config.HorarioConfigRepo;
import com.tesis.BackV2.request.HorarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HorarioServ {
    @Autowired
    private HorarioRepo horarioRepo;
    @Autowired
    private DistributivoRepo distributivoRepo;
    @Autowired
    private HorarioConfigRepo horarioConfigRepo;

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
        // Traer la config del horario
        HorarioConfig horarioConfig = horarioConfigRepo.findById(request.getIdHoraConfig())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("La configuración del horario no ha sido encontrada")
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

        if(validarDistributivoHoras(request, 0) || validarHorario(request)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El docente ya tiene un horario asignado en ese día y hora")
                    .build()
            );
        }

        // Traer las horas de la materia
        int horasMateria = distributivo.getMateria().getHoras();

        if (distributivo.getHorasAsignadas() == horasMateria) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas asignadas ya es igual a las horas de la materia")
                    .build()
            );
        }

        // Obtener la cantidad de horas
        int cantHoras = distributivo.getHorasAsignadas() + 1;

        if (cantHoras > horasMateria) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas asignadas supera las horas de la materia")
                    .build()
            );
        }

        // Actualizar las horas asignadas
        distributivo.setHorasAsignadas(cantHoras);
        distributivoRepo.save(distributivo);

        // Crear y guardar el horario
        Horario horario = Horario.builder()
                .cantHoras(cantHoras)
                .diaSemana(request.getDiaSemana())
                .horario(horarioConfig)
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
        int cantHoras = 0;

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
                        .ciclo(horario.getDistributivo().getCiclo().getNombre())
                        .curso(horario.getDistributivo().getCurso().getGrado().getNombre() + " " + horario.getDistributivo().getCurso().getParalelo())
                        .materia(horario.getDistributivo().getMateria().getNombre())
                        .docente(horario.getDistributivo().getDocente().getUsuario().getNombres() + " " + horario.getDistributivo().getDocente().getUsuario().getApellidos())
                        .build()
                )
                .toList();
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
                    && horario.getDistributivo().getMateria() == distributivo.getMateria()
                    && horario.getDistributivo().getCiclo() == distributivo.getCiclo()
                    && horario.getHorario().getId() == request.getIdHoraConfig()
                    && horario.getId() != idHorario) {

                return true;

            }
        }

        return false;
    }

    private boolean validarHorario(HorarioRequest request){
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
            if(horario.getDiaSemana().equals(request.getDiaSemana())
                    && horario.getDistributivo().getCiclo() == distributivo.getCiclo()
                    && horario.getDistributivo().getCurso() == distributivo.getCurso()
                    && horario.getHorario().getId() == request.getIdHoraConfig()
                    && horario.getId() != 0) {

                return true;

            }
        }

        return false;
    }

}
