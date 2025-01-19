package com.tesis.BackV2.services;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.asistencia.*;
import com.tesis.BackV2.dto.dashboard.CantidadesDTO;
import com.tesis.BackV2.entities.Asistencia;
import com.tesis.BackV2.entities.Distributivo;
import com.tesis.BackV2.entities.Matricula;
import com.tesis.BackV2.enums.EstadoAsistencia;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.AsistenciaRequest;
import com.tesis.BackV2.request.CicloARequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
                    .distributivo(repoDist.findById(asistenciaRequest.getDistributivoID()).orElseThrow(() ->
                            new ApiException(ApiResponse.<String> builder()
                                    .error(true)
                                    .codigo(404)
                                    .mensaje("No se encontró el distributivo")
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

    public ApiResponse<String> actualizarAsistencia(List<AsistenciaRequest> requests) {
        for (AsistenciaRequest request : requests) {
            Asistencia asistencia = repo.findById(request.getIdAsistencia()).orElseThrow(() ->
                    new ApiException(ApiResponse.<String> builder()
                            .error(true)
                            .codigo(404)
                            .mensaje("No se encontró la asistencia con ID: " + request.getIdAsistencia())
                            .build()));

            asistencia.setEstado(request.getEstado());
            asistencia.setObservaciones(request.getObservaciones());

            repo.save(asistencia);
        }

        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Solicitud exitosa")
                .detalles("Todas las asistencias se actualizaron correctamente")
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
                        .diaSemana(String.valueOf(asistencia.getFecha().getDayOfWeek()))
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

    // visualización de las asistencias de los estudiantes de un distributivo
    @Transactional
    public AsistenciasDocenteDTO asistenciasByDistributivoFecha(long idDis, LocalDate fecha) {
        List<Asistencia> asisList = repo.findByDistributivo_IdAndFecha(idDis, fecha);

        if (asisList.isEmpty()) {
            return null;
        }

        int totalAsistencias = asisList.size(), asistio = 0, falta = 0, justificado = 0;

        for (Asistencia asistencia : asisList) {
            if (asistencia.getEstado().name().equals("Presente")) asistio++;
            if (asistencia.getEstado().name().equals("Ausente")) falta++;
            if (asistencia.getEstado().name().equals("Justificado")) justificado++;
        }

        double porcentajeAsistencias = (double) (asistio * 100) / totalAsistencias;
        double porcentajeFaltas = (double) (falta * 100) / totalAsistencias;
        double porcentajeJustificadas = (double) (justificado * 100) / totalAsistencias;

        return AsistenciasDocenteDTO.builder()
                .diaSemana(String.valueOf(asisList.getFirst().getFecha().getDayOfWeek()))
                .datos(PorcentAsisEst.builder()
                        .totalAsistencias(totalAsistencias)
                        .totalFaltas(falta)
                        .totalJustificadas(justificado)
                        .totalAsist(asistio)
                        .porcentajeAsistencias(asistio == 0 ? 0 : porcentajeAsistencias)
                        .porcentajeFaltas(falta == 0 ? 0 : porcentajeFaltas)
                        .porcentajeJustificadas(justificado == 0 ? 0 : porcentajeJustificadas)
                        .build())
                .asistencias(asisList.stream().map(asistencia1 -> AsisDocDTO.builder()
                                .cedulaEstudiante(asistencia1.getEstudiante().getUsuario().getCedula())
                                .idEstudiante(asistencia1.getEstudiante().getId())
                        .idAsistencia(asistencia1.getId())
                        .estado(asistencia1.getEstado())
                        .apellidosEstudiante(asistencia1.getEstudiante().getUsuario().getApellidos())
                        .nombresEstudiante(asistencia1.getEstudiante().getUsuario().getNombres())
                        .observaciones(asistencia1.getObservaciones())
                        .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // traer total y porcentaje de asistencias por ciclo académico
    public CantidadesDTO cantAsisTotalCiclo(){
        // Traer las asistencias por ciclo académico
        List<Asistencia> asistencias = repo.findByCicloAcademico_Id(repoCicl.findByActivoTrue().getId());

        // Contar asistencias por estado
        int totalAsistencias = asistencias.size();
        int asistio = 0, falta = 0, justificado = 0;

        for (Asistencia asistencia : asistencias) {
            if (asistencia.getEstado().name().equals("Presente")) asistio++;
            if (asistencia.getEstado().name().equals("Ausente")) falta++;
            if (asistencia.getEstado().name().equals("Justificado")) justificado++;
        }

        // Calcular porcentajes
        double porcentajeAsistencias = (double) (asistio * 100) / totalAsistencias;
        double porcentajeFaltas = (double) (falta * 100) / totalAsistencias;
        double porcentajeJustificadas = (double) (justificado * 100) / totalAsistencias;

        return CantidadesDTO.builder()
                .total(totalAsistencias)
                .completo(asistio)
                .incompleto(falta)
                .reservado(justificado)
                .porcentajeCompleto(asistio == 0 ? 0 : porcentajeAsistencias)
                .porcentajeIncompleto(falta == 0 ? 0 : porcentajeFaltas)
                .porcentajeReservado(justificado == 0 ? 0 : porcentajeJustificadas)
                .build();

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
