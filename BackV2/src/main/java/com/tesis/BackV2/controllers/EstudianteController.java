package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.request.DistributivoRequest;
import com.tesis.BackV2.services.cicloacademico.DistributivoServ;
import com.tesis.BackV2.services.contenido.TemaService;
import com.tesis.BackV2.services.contenido.UnidadServ;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudiante/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class EstudianteController {

    private final DistributivoServ disServ;
    private final UnidadServ uniServ;
    private final TemaService temaServ;

    private final JwtService jwtService;

    private final EstudianteRepo repEst;

    /*  ---------------------------- Materias  ---------------------------- */
    // Traer Materia por curso del Estudiante
    @GetMapping("materias")
    public ResponseEntity<?> listarMaterias(HttpServletRequest request) {

        Estudiante estudiante = validarEstudiante(request);
        if (estudiante == null) return buildErrorResponse("No se encontró el estudiante", 404);

        return ResponseEntity.ok(disServ.getDistributivoByCurso(estudiante.getMatricula().getCurso().getId()));

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
