package com.tesis.BackV2.services;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.entities.Asistencia;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.AsistenciaRepo;
import com.tesis.BackV2.repositories.CicloAcademicoRepo;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.HorarioRepo;
import com.tesis.BackV2.request.AsistenciaRequest;
import com.tesis.BackV2.request.CicloARequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsistenciaServ {

    private final AsistenciaRepo repo;
    private final EstudianteRepo repoEst;
    private final HorarioRepo repoHor;
    private final CicloAcademicoRepo repoCicl;

    // registrar Asistencia
    @Transactional
    public ApiResponse<String> registrarAsistencia(AsistenciaRequest request, List<AsistenciaRequest> requests) {

        for (AsistenciaRequest asistenciaRequest : requests) {
            validarRequest(asistenciaRequest);

            repo.save(Asistencia.builder()
                    .estado(asistenciaRequest.getEstado())
                    .fecha(asistenciaRequest.getFecha())
                    .observaciones(asistenciaRequest.getObservaciones())
                    .estudiante(repoEst.findByUsuarioCedula(request.getCedulaEstudiante()))
                    .horario(repoHor.findById(request.getHorarioID()).orElseThrow(() ->
                            new ApiException(ApiResponse.<String> builder()
                                    .error(true)
                                    .codigo(400)
                                    .mensaje("Error de validación")
                                    .detalles("El horario no existe")
                                    .build())))
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
