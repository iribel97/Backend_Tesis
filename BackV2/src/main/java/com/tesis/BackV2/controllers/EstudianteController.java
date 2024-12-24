package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.dto.contenido.EntregaDTO;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.MatriculaRepo;
import com.tesis.BackV2.request.contenido.EntregaRequest;
import com.tesis.BackV2.services.cicloacademico.DistributivoServ;
import com.tesis.BackV2.services.contenido.*;
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
    private final UnidadServ uniServ;
    private final TemaServ temaServ;
    private final MaterialApoyoServ matServ;
    private final AsignacionServ asigServ;
    private final EntregaServ entServ;

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

    // Traer una Materia que cursa un estudiante
    @GetMapping("materia/{idDistributivo}")
    public ResponseEntity<?> obtenerMateria(@PathVariable Long idDistributivo) {
        return ResponseEntity.ok(disServ.obtenerDistributivo(idDistributivo));
    }

    // Visualizar Unidades de una Materia activa
    @GetMapping("materia/unidades/{idDistributivo}")
    public ResponseEntity<?> listarUnidadesActivas(@PathVariable Long idDistributivo) {
        return ResponseEntity.ok(uniServ.obtenerUnidadesActivas(idDistributivo));
    }

    // Visualizar una unidad
    @GetMapping("materia/unidad/{idUnidad}")
    public ResponseEntity<?> obtenerUnidad(@PathVariable Long idUnidad) {
        return ResponseEntity.ok(uniServ.obtenerUnidadActiva(idUnidad));
    }

    // Visualizar Temas de una Unidad activa
    @GetMapping("materia/unidad/temas/{idUnidad}")
    public ResponseEntity<?> listarTemasActivos(@PathVariable Long idUnidad) {
        return ResponseEntity.ok(temaServ.obtenerTemasActivos(idUnidad));
    }

    // Visualizar un tema
    @GetMapping("materia/unidad/tema/{idTema}")
    public ResponseEntity<?> obtenerTema(@PathVariable Long idTema) {
        return ResponseEntity.ok(temaServ.obtenerTemaActivo(idTema));
    }

    // Visualizar Material de Apoyo de un tema
    @GetMapping("materia/unidad/tema/material/{idTema}")
    public ResponseEntity<?> obtenerMaterialApoyo(@PathVariable Long idTema) {
        return ResponseEntity.ok(matServ.obtenerPorTemaActivo(idTema, true));
    }

    // Visualizar Asignaciones de un tema activo
    @GetMapping("materia/unidad/tema/asignaciones/{idTema}")
    public ResponseEntity<?> listarAsignacionesActivas(@PathVariable Long idTema) {
        return ResponseEntity.ok(asigServ.traerPorTemaActivo(idTema, true));
    }

    // Agregar entrega a una asignación
    @PostMapping("/asignacion/entrega")
    public ResponseEntity<ApiResponse<?>> agregarEntrega(@RequestBody EntregaRequest request) {
        return ResponseEntity.ok(entServ.editarEntrega(request));
    }

    // Eliminar entrega de una asignación
    @PutMapping("/asignacion/entrega/{idEntrega}")
    public ResponseEntity<ApiResponse<?>> eliminarEntrega(@PathVariable Long idEntrega) {
        return ResponseEntity.ok(entServ.eliminarEntrega(idEntrega));
    }

    // Visualizar entregas de una asignación
    @GetMapping("/asignacion/entregas/{idAsignacion}/{estudianteId}")
    public ResponseEntity<?> listarEntregas(@PathVariable Long idAsignacion, @PathVariable String estudianteId) {
        Estudiante estudiante = repEst.findByUsuarioCedula(estudianteId);
        return ResponseEntity.ok(entServ.traerPorAsignacionYEstudiante(idAsignacion, estudiante.getId()));
    }

    // Visualizar una entrega
    @GetMapping("/asignacion/entrega/{idEntrega}")
    public ResponseEntity<EntregaDTO> obtenerEntrega(@PathVariable Long idEntrega) {
        return ResponseEntity.ok(entServ.traerPorId(idEntrega));
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
