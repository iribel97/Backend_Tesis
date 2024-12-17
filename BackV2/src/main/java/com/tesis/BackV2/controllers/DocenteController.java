package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.repositories.DocenteRepo;
import com.tesis.BackV2.request.DistributivoRequest;
import com.tesis.BackV2.services.cicloacademico.DistributivoServ;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/docente/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class DocenteController {

    private final JwtService jwtService;

    private final DistributivoServ disServ;

    private final DocenteRepo repDocente;
    /*  ---------------------------- Gestión de Asistencia  ---------------------------- */

    /*  ---------------------------- Gestión de Conducta  ---------------------------- */

    /*  ---------------------------- Gestión de Citaciones  ---------------------------- */

    /*  ---------------------------- Reporte de Calificaciones  ---------------------------- */

    /*  ---------------------------- Materias  ---------------------------- */
    // Traer Materia por docente
    @GetMapping("materias")
    public ResponseEntity<?> listarMaterias(HttpServletRequest request) {

        Docente docente = validarDocente(request);
        if (docente == null) return buildErrorResponse("No se encontró el docente", 404);


        return ResponseEntity.ok(disServ.getDistributivoByDocente(docente.getUsuario().getCedula()));

    }

    // Traer una Materia que imparte un docente
    @GetMapping("materia/{idDistributivo}")
    public ResponseEntity<?> obtenerMateria(@PathVariable Long idDistributivo) {
        return ResponseEntity.ok(disServ.obtenerDistributivo(idDistributivo));
    }

    /*  ---------------------------- Visualización de Horario  ---------------------------- */


    /* ---------------------------- Métodos Privados ---------------------------- */
    // Valida y extrae el docente del token
    private Docente validarDocente(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        String userCedula = jwtService.extractUsername(token);
        return repDocente.findByUsuarioCedula(userCedula);
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
