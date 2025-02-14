package com.tesis.BackV2.services;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.asistencia.*;
import com.tesis.BackV2.dto.dashboard.CantAsisTutorDTO;
import com.tesis.BackV2.dto.dashboard.CantidadesDTO;
import com.tesis.BackV2.dto.dashboard.EstudianteFaltasDTO;
import com.tesis.BackV2.entities.*;
import com.tesis.BackV2.enums.EstadoAsistencia;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.infra.MensajeHtml;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.AsistenciaRequest;
import com.tesis.BackV2.request.CicloARequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsistenciaServ {
    @Autowired
    private CorreoServ emailService;

    private final MensajeHtml mensaje = new MensajeHtml();

    private final AsistenciaRepo repo;
    private final EstudianteRepo repoEst;
    private final RepresentanteRepo repoRep;
    private final HorarioRepo repoHor;
    private final CicloAcademicoRepo repoCicl;
    private final MatriculaRepo repoMatr;
    private final DistributivoRepo repoDist;

    // registrar Asistencia
    @Transactional
    public ApiResponse<String> registrarAsistencia(List<AsistenciaRequest> requests) {

        for (AsistenciaRequest asistenciaRequest : requests) {
            validarRequest(asistenciaRequest);
            Estudiante est = repoEst.findByUsuarioCedula(asistenciaRequest.getCedulaEstudiante());
            Distributivo dis = repoDist.findById(asistenciaRequest.getDistributivoID()).orElseThrow(() ->
                    new ApiException(ApiResponse.<String> builder()
                            .error(true)
                            .codigo(404)
                            .mensaje("No se encontró el distributivo")
                            .build()));

            if(asistenciaRequest.getEstado().equals(EstadoAsistencia.Ausente)){
                String nombresEstudiante = est.getUsuario().getApellidos() + " " + est.getUsuario().getNombres();
                String destinatario = est.getRepresentante().getUsuario().getEmail();
                String asunto = "Falta a clases";
                String messaje = mensaje.mensajeFaltaClases(nombresEstudiante,
                        dis.getMateria().getNombre(),
                        String.valueOf(asistenciaRequest.getFecha()) );

                emailService.enviarCorreo(destinatario, asunto, messaje);

            }

            repo.save(Asistencia.builder()
                    .estado(asistenciaRequest.getEstado())
                    .fecha(asistenciaRequest.getFecha())
                    .observaciones(asistenciaRequest.getObservaciones())
                    .estudiante(est)
                    .distributivo(dis)
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

            if(request.getEstado().equals(EstadoAsistencia.Ausente)){
                String nombresEstudiante = asistencia.getEstudiante().getUsuario().getApellidos() + " " + asistencia.getEstudiante().getUsuario().getNombres();
                String destinatario = asistencia.getEstudiante().getRepresentante().getUsuario().getEmail();
                String asunto = "Falta a clases";
                String messaje = mensaje.mensajeFaltaClases(nombresEstudiante,
                        asistencia.getDistributivo().getMateria().getNombre(),
                        String.valueOf(asistencia.getFecha()) );

                emailService.enviarCorreo(destinatario, asunto, messaje);

            }

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

    // viualización de un distributivo
    public AsistenciasEstDTO asistenciaDis (long idDis) {
        List<Asistencia> asistencias = repo.findByDistributivo_Id(idDis);

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

    // Visualización general de asistencia por materias que imparte el docente
    public  List<AsistenciasDisEstDTO> asistenciasByDistributivoDocente(String cedulaDoc){
        Collection<Distributivo> materias = repoDist.findByCicloIdAndDocente_Usuario_Cedula(repoCicl.findByActivoTrue().getId(), cedulaDoc);
        List<AsistenciasDisEstDTO> asistencias = new ArrayList<>();

        for (Distributivo materia : materias) {
            asistencias.add(AsistenciasDisEstDTO.builder()
                    .nombreMateria(materia.getMateria().getNombre())
                    .curso(materia.getCurso().getGrado().getNombre() + " " + materia.getCurso().getParalelo())
                    .data(asistenciaDis(materia.getId()).getDatos())
                    .build());
        }

        return asistencias;
    }

    // visualiazción de todas las asistencias de un estudiante
    public AsistenciasEstudianteRepDTO asisGeneralEstRep(Estudiante estudiante) {
        // Matricula del estudiante
        Matricula matricula = repoMatr.findByEstudiante_IdAndCiclo_Activo(estudiante.getId(), true);
        // listar asistencias por estudiante
        List<Asistencia> asistencias = repo.findByEstudianteAndCicloActivo(estudiante.getId());
        // Inicializar el array de retorno
        AsistenciasEstudianteRepDTO asisEst = new AsistenciasEstudianteRepDTO();
        // Inicializar variables
        int totalAsistencias = asistencias.size();
        int asistio = 0, falta = 0, tardanza = 0;
        double porcentajeAsistencias = 0, porcentajeFaltas = 0, porcentajeJustificadas = 0;

        if (totalAsistencias > 0) {
            // Contar asistencias por estado
            for (Asistencia asistencia : asistencias) {
                if (asistencia.getEstado().name().equals("Presente")) asistio++;
                if (asistencia.getEstado().name().equals("Ausente")) falta++;
                if (asistencia.getEstado().name().equals("Justificado")) tardanza++;
            }
            // Calcular porcentajes
            porcentajeAsistencias = (double) (asistio * 100) / totalAsistencias;
            porcentajeFaltas = (double) (falta * 100) / totalAsistencias;
            porcentajeJustificadas = (double) (tardanza * 100) / totalAsistencias;
        }

        return AsistenciasEstudianteRepDTO.builder()
                .apellidos(estudiante.getUsuario().getApellidos())
                .nombres(estudiante.getUsuario().getNombres())
                .curso(matricula.getCurso().getGrado().getNombre() + " " + matricula.getCurso().getParalelo())
                .data(PorcentAsisEst.builder()
                        .totalAsistencias(totalAsistencias)
                        .totalFaltas(falta)
                        .totalJustificadas(tardanza)
                        .totalAsist(asistio)
                        .porcentajeAsistencias(asistio == 0 ? 0 : porcentajeAsistencias)
                        .porcentajeFaltas(falta == 0 ? 0 : porcentajeFaltas)
                        .porcentajeJustificadas(tardanza == 0 ? 0 : porcentajeJustificadas)
                        .build())
                .build();

    }

    // visualizar asistencias de todos los estudiantes de un representante
    public List<AsistenciasEstudianteRepDTO> asisGeneralRep(String cedulaRep) {
        Representante representante = repoRep.findByUsuarioCedula(cedulaRep);
        // Traer los estudiantes del representante
        List<Estudiante> estudiantes = repoEst.findByRepresentanteId(representante.getId());
        // Inicializar el array de retorno
        List<AsistenciasEstudianteRepDTO> asistencias = new ArrayList<>();

        for (Estudiante estudiante : estudiantes) {
            asistencias.add(asisGeneralEstRep(estudiante));
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
            .asistencias(asisList.stream()
                    .sorted(Comparator.comparing(asistencia1 -> asistencia1.getEstudiante().getUsuario().getApellidos()))
                    .map(asistencia1 -> AsisDocDTO.builder()
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

    // traer por curso
    public CantidadesDTO asistenciasByCurso(long cursoId) {
        List<Asistencia> asistencias = repo.findByDistributivo_Curso_Id(cursoId);

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

    // traer estudiante con mas faltas de un curso
    public EstudianteFaltasDTO estudianteConMasFaltas(long cursoId) {
        List<Object[]> faltas = repo.findEstudianteConMasFaltas(cursoId, EstadoAsistencia.Ausente);

        if (faltas.isEmpty()) {
            return null;
        }

        return EstudianteFaltasDTO.builder()
                .apellidos(String.valueOf(faltas.get(0)[0]))
                .nombres(String.valueOf(faltas.get(0)[1]))
                .faltas(Integer.parseInt(String.valueOf(faltas.get(0)[2])))
                .build();
    }

    // asistencia por distributivo de un curso
    public List<CantAsisTutorDTO> asisPorDisDeUnCurso(long idCurso) {
        List<Distributivo> distributivos = repoDist.findByCursoId(idCurso);
        List<CantAsisTutorDTO> asistencias = new ArrayList<>();

        for (Distributivo distributivo : distributivos) {
            List<Asistencia> asistenciasList = repo.findByDistributivo_Id(distributivo.getId());
            int asistio = 0, falta = 0, justificado = 0;

            if ( !asistenciasList.isEmpty()) {
                for (Asistencia asistencia : asistenciasList) {
                    if (asistencia.getEstado().name().equals("Presente")) asistio++;
                    if (asistencia.getEstado().name().equals("Ausente")) falta++;
                    if (asistencia.getEstado().name().equals("Justificado")) justificado++;
                }


            }

            asistencias.add(CantAsisTutorDTO.builder()
                    .materia(distributivo.getMateria().getNombre())
                    .asistidos(asistio)
                    .faltas(falta)
                    .justificados(justificado)
                    .build());
        }

        return asistencias;
    }

    // promedio de asistencias por ciclo académico y id estudiante
    public double promedioAsistencias(long idEst) {
        List<AsistenciasDisEstDTO> asistencias = asistenciasByDistributivo(repoMatr.findTopByEstudianteIdOrderByIdDesc(idEst).getEstudiante().getUsuario().getCedula());

        double asistio = 0;

        for (AsistenciasDisEstDTO asistencia : asistencias) {
            asistio += asistencia.getData().getPorcentajeAsistencias();
        }

        return asistio /asistencias.size();
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
