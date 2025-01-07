package com.tesis.BackV2.services;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.asistencia.AsistenciaEstDTO;
import com.tesis.BackV2.dto.asistencia.AsistenciasDisEstDTO;
import com.tesis.BackV2.dto.asistencia.AsistenciasEstDTO;
import com.tesis.BackV2.dto.asistencia.PorcentAsisEst;
import com.tesis.BackV2.entities.Asistencia;
import com.tesis.BackV2.entities.Distributivo;
import com.tesis.BackV2.entities.Matricula;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.AsistenciaRequest;
import com.tesis.BackV2.request.CicloARequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsistenciaServ {

    private final AsistenciaRepo repo;
    private final EstudianteRepo repoEst;
    private final HorarioRepo repoHor;
    private final CicloAcademicoRepo repoCicl;
    private final MatriculaRepo repoMatr;
    private final DistributivoRepo repoDist;

    // registrar Asistencia
    @Transactional
    public ApiResponse<String> registrarAsistencia(List<AsistenciaRequest> requests) {

        for (AsistenciaRequest asistenciaRequest : requests) {
            validarRequest(asistenciaRequest);

            repo.save(Asistencia.builder()
                    .estado(asistenciaRequest.getEstado())
                    .fecha(asistenciaRequest.getFecha())
                    .observaciones(asistenciaRequest.getObservaciones())
                    .estudiante(repoEst.findByUsuarioCedula(asistenciaRequest.getCedulaEstudiante()))
                    .horario(repoHor.findById(asistenciaRequest.getHorarioID()).orElseThrow(() ->
                            new ApiException(ApiResponse.<String> builder()
                                    .error(true)
                                    .codigo(400)
                                    .mensaje("Error de validación")
                                    .detalles("El horario no existe")
                                    .build())))
                    .cicloAcademico(repoCicl.findByActivoTrue())
                    .build());
        }



        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Solicitud exitosa")
                .detalles("Asistencia registrada correctamente")
                .build();
    }

    // Actualizar Asistencia
    @Transactional
    public ApiResponse<String> actualizarAsistencia(AsistenciaRequest request) {

        validarRequest(request);

        Asistencia asistencia = repo.findById(request.getId()).orElseThrow(() ->
                new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Error de validación")
                        .detalles("La asistencia no existe")
                        .build()));

        asistencia.setEstado(request.getEstado());
        asistencia.setFecha(request.getFecha());
        asistencia.setEstudiante(repoEst.findByUsuarioCedula(request.getCedulaEstudiante()));
        asistencia.setHorario(repoHor.findById(request.getHorarioID()).orElseThrow(() ->
                new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Error de validación")
                        .detalles("El horario no existe")
                        .build())));

        repo.save(asistencia);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Solicitud exitosa")
                .detalles("Asistencia actualizada correctamente")
                .build();
    }

    // Estudiante visualiza su asistencia
    public AsistenciasEstDTO asistenciaEstudiante (long idEst, long idDis) {
        List<Asistencia> asistencias = repo.findByEstudianteAndDistributivo(idEst, idDis);

        int totalAsistencias = asistencias.size();
        int asistio = 0, falta = 0, tardanza = 0;
        double porcentajeAsistencias, porcentajeFaltas, porcentajeJustificadas;

        for (Asistencia asistencia : asistencias) {
            if (asistencia.getEstado().name().equals("Presente")) asistio++;
            if (asistencia.getEstado().name().equals("Ausente")) falta++;
            if (asistencia.getEstado().name().equals("Justificado")) tardanza++;
        }

        porcentajeAsistencias = (double) (asistio * 100) / totalAsistencias;
        porcentajeFaltas = (double) (falta * 100) / totalAsistencias;
        porcentajeJustificadas = (double) (tardanza * 100) / totalAsistencias;

        return AsistenciasEstDTO.builder()
                .datos(PorcentAsisEst.builder()
                        .totalAsistencias(totalAsistencias)
                        .totalFaltas(falta)
                        .totalJustificadas(tardanza)
                        .totalAsist(asistio)
                        .porcentajeAsistencias(asistio == 0 ? 0 : porcentajeAsistencias)
                        .porcentajeFaltas(falta == 0 ? 0 : porcentajeFaltas)
                        .porcentajeJustificadas(tardanza == 0 ? 0 : porcentajeJustificadas)
                        .build())
                .asistencias(asistencias.stream().map(asistencia -> AsistenciaEstDTO.builder()
                        .id(asistencia.getId())
                        .estado(String.valueOf(asistencia.getEstado()))
                        .fecha(asistencia.getFecha())
                        .observaciones(asistencia.getObservaciones())
                        .diaSemana(String.valueOf(asistencia.getHorario().getDiaSemana()))
                        .build())
                        .collect(Collectors.toList()))
                .build();

    }

    // Visualización general de asistencia
    public List<AsistenciasDisEstDTO> asistenciasByDistributivo(String cedulaEst){
        Matricula x = repoMatr.findTopByEstudianteUsuarioCedulaOrderByIdDesc(cedulaEst);
        List<Distributivo> materias = repoDist.findByCursoId(x.getCurso().getId());
        List<AsistenciasDisEstDTO> asistencias = new ArrayList<>();

        for (Distributivo materia : materias) {
            asistencias.add(AsistenciasDisEstDTO.builder()
                    .nombreMateria(materia.getMateria().getNombre())
                    .data(asistenciaEstudiante(x.getEstudiante().getId(), materia.getId()).getDatos())
                    .build());
        }

        return asistencias;
    }

    /* ---- METODOS PROPIOS DEL SERVICIO ---- */
    // validar atributos del request
    private void validarRequest(AsistenciaRequest request) {
        if (request.getCedulaEstudiante() == null) {
            throw new ApiException(ApiResponse.<String> builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error de validación")
                    .detalles("El campo 'cedula estudiante' es obligatorio")
                    .build());
        }

        if (repoEst.findByUsuarioCedula(request.getCedulaEstudiante()) == null) {
            throw new ApiException(ApiResponse.<String> builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error de validación")
                    .detalles("El estudiante no existe")
                    .build());

        }

        // validar que la fecha se encuentre dentro del ciclo académico activo
        if (repoCicl.findByActivoTrue().getFechaInicio().isAfter(request.getFecha()) ||
                repoCicl.findByActivoTrue().getFechaFin().isBefore(request.getFecha())) {
            throw new ApiException(ApiResponse.<String> builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error de validación")
                    .detalles("La fecha no se encuentra dentro del ciclo académico activo")
                    .build());
        }
    }
}
