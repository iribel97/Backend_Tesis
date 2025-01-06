package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.repositories.DocenteRepo;
import com.tesis.BackV2.request.contenido.*;
import com.tesis.BackV2.services.CicloAcademicoServ;
import com.tesis.BackV2.services.ContenidoServ;
import com.tesis.BackV2.services.cicloacademico.SisCalifServ;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/docente/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class DocenteController {

    private final JwtService jwtService;

    private final CicloAcademicoServ cicloAServ;
    private final ContenidoServ contServ;
    private final SisCalifServ sisCalifServ;

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

        return ResponseEntity.ok(cicloAServ.getDistributivoByDocente(docente.getUsuario().getCedula()));

    }

    // Traer una Materia que imparte un docente
    @GetMapping("materia/{idDistributivo}")
    public ResponseEntity<?> obtenerMateria(@PathVariable Long idDistributivo, HttpServletRequest request) {
        Docente docente = validarDocente(request);
        if (docente == null) return buildErrorResponse("Docente no encontrado", 400);
        return ResponseEntity.ok(contServ.contenidoMateriaDocente(idDistributivo, docente.getUsuario().getCedula()));
    }

    // Agregar contenido a un distributivo
    @PostMapping("materia/contenido/unidad")
    public ResponseEntity<?> agregarUnidad (@RequestBody UnidadRequest request){
        return ResponseEntity.ok(contServ.crearUnidad(request));
    }

    // Actualizar una unidad
    @PutMapping("materia/contenido/unidad")
    public ResponseEntity<?> actualizarUnidad(@RequestBody UnidadRequest request){
        return ResponseEntity.ok(contServ.editarUnidad(request));
    }

    // Agregar tema a la unidad
    @PostMapping("materia/contenido/tema")
    public ResponseEntity<?> agregarTema (@RequestBody TemaRequest request){
        return ResponseEntity.ok(contServ.crearTema(request));
    }

    // Actualizar un tema
    @PutMapping("materia/contenido/tema")
    public ResponseEntity<?> actualizarTema(@RequestBody TemaRequest request){
        return ResponseEntity.ok(contServ.editarTema(request));
    }

    // Agregar material de apoyo
    @PostMapping("materia/contenido/material")
    public ResponseEntity<?> agregarMaterialApoyo(@RequestBody MaterialApoyoRequest request){
        return ResponseEntity.ok(contServ.crearMaterialApoyo(request));
    }

    // Editar material de apoyo
    @PutMapping("materia/contenido/material")
    public ResponseEntity<?> editarMaterialApoyo(@RequestBody MaterialApoyoRequest request){
        return ResponseEntity.ok(contServ.editarMaterialApoyo(request));
    }

    // Eliminar material de apoyo
    @DeleteMapping("materia/contenido/material/{idMaterial}")
    public ResponseEntity<?> eliminarMaterialApoyo(@PathVariable Long idMaterial){
        return ResponseEntity.ok(contServ.eliminarMaterialApoyo(idMaterial));
    }

    // Visualizar sistema calificaciones para asignatura
    @GetMapping("materia/calificaciones/{idDistributivo}")
    public ResponseEntity<?> obtenerSistemaCalificaciones(@PathVariable Long idDistributivo){
        return ResponseEntity.ok(sisCalifServ.traerPorCicloDocente(idDistributivo));
    }

    // Crear asignación
    @PostMapping("materia/asignacion")
    public ResponseEntity<?> crearAsignacion(@RequestBody AsignacionRequest request){
        return ResponseEntity.ok(contServ.crearAsignacion(request));
    }

    // Editar asignación
    @PutMapping("materia/asignacion")
    public ResponseEntity<?> editarAsignacion(@RequestBody AsignacionRequest request){
        return ResponseEntity.ok(contServ.editarAsignacion(request));
    }


    // ocultar asignación
    @PutMapping("materia/asignacion/ocultar/{idAsignacion}")
    public ResponseEntity<?> ocultarAsignacion(@PathVariable Long idAsignacion){
        return ResponseEntity.ok(contServ.cambiarVisualizacionAsig(idAsignacion, false));
    }

    // mostrar asignación
    @PutMapping("materia/asignacion/mostrar/{idAsignacion}")
    public ResponseEntity<?> mostrarAsignacion(@PathVariable Long idAsignacion){
        return ResponseEntity.ok(contServ.cambiarVisualizacionAsig(idAsignacion, true));
    }

    // calificar entrega
    @PutMapping("/entrega/calificar")
    public ResponseEntity<?> calificarEntrega(@RequestBody NotaRequest nota){
        return ResponseEntity.ok(contServ.calificarEntrega(nota));
    }


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
