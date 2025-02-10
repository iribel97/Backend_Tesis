package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.dto.dashboard.ContGeneralEstDTO;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.entities.Matricula;
import com.tesis.BackV2.entities.Representante;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.MatriculaRepo;
import com.tesis.BackV2.repositories.RepresentanteRepo;
import com.tesis.BackV2.request.contenido.EntregaRequest;
import com.tesis.BackV2.services.AsistenciaServ;
import com.tesis.BackV2.services.CicloAcademicoServ;
import com.tesis.BackV2.services.ContenidoServ;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/estudiante/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class EstudianteController {

    private final CicloAcademicoServ cicloAServ;
    private final ContenidoServ contServ;
    private final AsistenciaServ asistenciaServ;

    private final JwtService jwtService;

    private final EstudianteRepo repEst;
    private final RepresentanteRepo repRep;
    private final MatriculaRepo matrRepo;

    /*  ---------------------------- Dashboard  ---------------------------- */
    // visualización general representante
    @GetMapping("dashboard/representante")
    public ResponseEntity<?> obtenerDashboardRepresentante(HttpServletRequest request) {
        Representante representante = repRep.findByUsuarioCedula(jwtService.extractUsername(extractTokenFromRequest(request)));
        if (representante == null) return buildErrorResponse("No se encontró el representante", 404);

        List<Estudiante> estudiantes = repEst.findByRepresentanteId(representante.getId());

        if (estudiantes.isEmpty()) return buildErrorResponse("Aún no cuenta con estudiantes", 202);

        List<ContGeneralEstDTO> general = new ArrayList<>();

        for ( Estudiante x : estudiantes) {
            Matricula matricula = matrRepo.findTopByEstudianteIdOrderByIdDesc(x.getId());
            general.add(ContGeneralEstDTO.builder()
                    .nombreEstudiante(x.getUsuario().getApellidos() + " " + x.getUsuario().getNombres())
                    .curso(matricula.getCurso().getGrado().getNombre() + " " + matricula.getCurso().getParalelo())
                    .promedio(contServ.promedioCursoGeneralEst(x.getId()))
                    .porcentajeAsist(asistenciaServ.promedioAsistencias(x.getId()))
                    .build());
        }

        return ResponseEntity.ok(general);
    }

    //visualizar %asistencia y promedio
    @GetMapping("dashboard")
    public ResponseEntity<?> obtenerDashboard(HttpServletRequest request) {
        Estudiante estudiante = validarEstudiante(request);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(ContGeneralEstDTO.builder()
                        .promedio(contServ.promedioCursoGeneralEst(estudiante.getId()))
                        .porcentajeAsist(asistenciaServ.promedioAsistencias(estudiante.getId()))
                .build());
    }

    // visualizar entregas pendientes
    @GetMapping("dashboard/entregas")
    public ResponseEntity<?> obtenerEntregas(HttpServletRequest request) {
        Estudiante estudiante = validarEstudiante(request);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(contServ.estregasPendientesEst(estudiante.getId()));
    }

    // visualizar entregas pendientes por cedula de estudiante para que vea el representante
    @GetMapping("dashboard/representante/entregas/{cedula}")
    public ResponseEntity<?> obtenerEntregasRepresentante(@PathVariable String cedula) {
        Estudiante estudiante = repEst.findByUsuarioCedula(cedula);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(contServ.estregasPendientesEst(estudiante.getId()));
    }

    @GetMapping("dashboard/representante/notas/{cedulaEst}")
    public ResponseEntity<?> obtenerPromediosByRepresentante(@PathVariable String cedulaEst) {
        Estudiante estudiante = repEst.findByUsuarioCedula(cedulaEst);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(contServ.notasCursoEst(estudiante.getId()));
    }

    @GetMapping("horario")
    public ResponseEntity<?> obtenerHorarios(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        // Extraer el nombre de usuario (o cualquier dato que guardes en el token)
        String userCedula = jwtService.extractUsername(token);

        Estudiante estudiante = repEst.findByUsuarioCedula(userCedula);

        if (estudiante == null) {
            throw new ApiException( ApiResponse.<String>builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error de validación")
                    .detalles("Usuario no encontrado")
                    .build()
            );
        }

        return ResponseEntity.ok(cicloAServ.getHorariosByCurso(matrRepo.findTopByEstudianteIdOrderByIdDesc(estudiante.getId()).getCurso().getId()));
    }

    /*  ---------------------------- Materias  ---------------------------- */
    // Traer Materia por curso del Estudiante
    @GetMapping("materias")
    public ResponseEntity<?> listarMaterias(HttpServletRequest request) {
        Estudiante estudiante = validarEstudiante(request);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(cicloAServ.getDistributivoByCurso(matrRepo.findTopByEstudianteIdOrderByIdDesc(estudiante.getId()).getCurso().getId()));
    }

    // visualizar contenido de materia activo
    @GetMapping("materia/{idDistributivo}")
    public ResponseEntity<?> obtenerMateria(@PathVariable Long idDistributivo) {
        return ResponseEntity.ok(contServ.contenidoMateria(idDistributivo));
    }

    // visuqalizar asignacion
    @GetMapping("asignacion/{idAsignacion}")
    public ResponseEntity<?> obtenerAsignacion(@PathVariable Long idAsignacion, HttpServletRequest request) {
        Estudiante estudiante = validarEstudiante(request);
        if (estudiante == null) return buildErrorResponse("Solicitud Inválida por usuario no encontrado", 404);

        return ResponseEntity.ok(contServ.traerPorId(idAsignacion, estudiante.getId()));
    }


    // Agregar entrega a una asignación
    @PutMapping("asignacion/entrega")
    public ResponseEntity<ApiResponse<?>> agregarEntrega(@RequestBody EntregaRequest request) {
        return ResponseEntity.ok(contServ.editarEntrega(request));
    }

    // Eliminar entrega de una asignación
    @PutMapping("asignacion/entrega/{idEntrega}")
    public ResponseEntity<ApiResponse<?>> eliminarEntrega(@PathVariable Long idEntrega) {
        return ResponseEntity.ok(contServ.eliminarEntrega(idEntrega));
    }

    /*  ---------------------------- Visualización de Calificaciones  ---------------------------- */

    // Visualizar calificaciones por distributivo y id de estudiante
    @GetMapping("calificaciones/{distriID}")
    public ResponseEntity<?> obtenerCalificaciones(HttpServletRequest request, @PathVariable Long distriID) {
        Estudiante estudiante = validarEstudiante(request);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(contServ.entregasPorDisYEst( distriID, estudiante.getId()));
    }

    // visualizar notas por id estudiante
    @GetMapping("notas")
    public ResponseEntity<?> obtenerNotas(HttpServletRequest request) {
        Estudiante estudiante = validarEstudiante(request);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(contServ.notasCursoEst(estudiante.getId()));
    }

    /*  ---------------------------- Visualización de Compañeros  ---------------------------- */

    /*  ---------------------------- Visualización de Conducta  ---------------------------- */

    /*  ---------------------------- Visualización de Asistencia  ---------------------------- */
    // Visualizar asistencia
    @GetMapping("asistencia/{distriID}")
    public ResponseEntity<?> obtenerAsistencia(HttpServletRequest request, @PathVariable Long distriID) {
        Estudiante estudiante = validarEstudiante(request);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(asistenciaServ.asistenciaEstudiante(estudiante.getId(), distriID));
    }

    // Visualización general de asistencia
    @GetMapping("asistencias")
    public ResponseEntity<?> obtenerAsistencias(HttpServletRequest request) {
        Estudiante estudiante = validarEstudiante(request);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(asistenciaServ.asistenciasByDistributivo(estudiante.getUsuario().getCedula()));
    }

    /* ---------------------------- Métodos Privados ---------------------------- */
    // Valida y extrae el docente del token
    private Estudiante validarEstudiante(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        String userCedula = jwtService.extractUsername(token);
        return repEst.findByUsuarioCedula(userCedula);
    }

    // Construye respuestas de error consistentes
    private ResponseEntity<ApiResponse<?>> buildErrorResponse(String detalles, int codigo) {
        return ResponseEntity.status(codigo).body(
                ApiResponse.builder()
                        .error(true)
                        .mensaje("Error")
                        .codigo(codigo)
                        .detalles(detalles)
                        .build()
        );
    }
    // Metodo para extraer el token del encabezado de la solicitud
    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remover el prefijo "Bearer "
        }
        throw new RuntimeException("Token no encontrado o inválido");
    }
}
