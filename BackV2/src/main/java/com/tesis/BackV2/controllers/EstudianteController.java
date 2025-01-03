package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.MatriculaRepo;
import com.tesis.BackV2.request.contenido.EntregaRequest;
import com.tesis.BackV2.services.ContenidoServ;
import com.tesis.BackV2.services.cicloacademico.DistributivoServ;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiante/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class EstudianteController {

    private final DistributivoServ disServ;
    private final ContenidoServ contServ;

    private final JwtService jwtService;

    private final EstudianteRepo repEst;
    private final MatriculaRepo matrRepo;

    /*  ---------------------------- Materias  ---------------------------- */
    // Traer Materia por curso del Estudiante
    @GetMapping("materias")
    public ResponseEntity<?> listarMaterias(HttpServletRequest request) {
        Estudiante estudiante = validarEstudiante(request);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(disServ.getDistributivoByCurso(matrRepo.findTopByEstudianteIdOrderByIdDesc(estudiante.getId()).getCurso().getId()));
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

    // Visualizar entregas de una asignación
    @GetMapping("asignacion/entregas/{idAsignacion}/{estudianteId}")
    public ResponseEntity<?> listarEntregas(@PathVariable Long idAsignacion, @PathVariable String estudianteId) {
        Estudiante estudiante = repEst.findByUsuarioCedula(estudianteId);
        return ResponseEntity.ok(contServ.traerPorAsignacionYEstudiante(idAsignacion, estudiante.getId()));
    }

    /*  ---------------------------- Visualización de Calificaciones  ---------------------------- */

    /*  ---------------------------- Visualización de Compañeros  ---------------------------- */

    /*  ---------------------------- Visualización de Conducta  ---------------------------- */

    /*  ---------------------------- Visualización de Asistencia  ---------------------------- */

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
