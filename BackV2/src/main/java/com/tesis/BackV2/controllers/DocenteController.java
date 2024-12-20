package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.repositories.DocenteRepo;
import com.tesis.BackV2.request.DistributivoRequest;
import com.tesis.BackV2.request.contenido.MaterialApoyoRequest;
import com.tesis.BackV2.request.contenido.TemaRequest;
import com.tesis.BackV2.request.contenido.UnidadRequest;
import com.tesis.BackV2.services.cicloacademico.DistributivoServ;
import com.tesis.BackV2.services.contenido.MaterialApoyoServ;
import com.tesis.BackV2.services.contenido.TemaService;
import com.tesis.BackV2.services.contenido.UnidadServ;
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
    private final UnidadServ uniServ;
    private final TemaService temaServ;
    private final MaterialApoyoServ matServ;

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

    // Agregar contenido a un distributivo
    @PostMapping("materia/contenido/unidad")
    public ResponseEntity<?> agregarUnidad (@RequestBody UnidadRequest request){
        return ResponseEntity.ok(uniServ.crearUnidad(request));
    }

    // Traer unidades de un distributivo
    @GetMapping("materia/contenido/unidades/{idDistributivo}")
    public ResponseEntity<?> obtenerUnidades(@PathVariable Long idDistributivo){
        return ResponseEntity.ok(uniServ.obtenerUnidades(idDistributivo));
    }

    // Traer una unidad
    @GetMapping("materia/contenido/unidad/{idUnidad}")
    public ResponseEntity<?> obtenerUnidad(@PathVariable Long idUnidad){
        return ResponseEntity.ok(uniServ.obtenerUnidad(idUnidad));
    }

    // Actualizar una unidad
    @PutMapping("materia/contenido/unidad")
    public ResponseEntity<?> actualizarUnidad(@RequestBody UnidadRequest request){
        return ResponseEntity.ok(uniServ.editarUnidad(request));
    }

    // Eliminar una unidad
    @DeleteMapping("materia/contenido/unidad/{idUnidad}")
    public ResponseEntity<?> eliminarUnidad(@PathVariable Long idUnidad){
        return ResponseEntity.ok(uniServ.eliminarUnidad(idUnidad));
    }

    // Agregar tema a la unidad
    @PostMapping("materia/contenido/tema")
    public ResponseEntity<?> agregarTema (@RequestBody TemaRequest request){
        return ResponseEntity.ok(temaServ.crearTema(request));
    }

    // Traer temas de una unidad
    @GetMapping("materia/contenido/temas/{idUnidad}")
    public ResponseEntity<?> obtenerTemas(@PathVariable Long idUnidad){
        return ResponseEntity.ok(temaServ.obtenerTemas(idUnidad));
    }

    // Traer un tema
    @GetMapping("materia/contenido/tema/{idTema}")
    public ResponseEntity<?> obtenerTema(@PathVariable Long idTema){
        return ResponseEntity.ok(temaServ.obtenerTema(idTema));
    }

    // Actualizar un tema
    @PutMapping("materia/contenido/tema")
    public ResponseEntity<?> actualizarTema(@RequestBody TemaRequest request){
        return ResponseEntity.ok(temaServ.editarTema(request));
    }

    // Eliminar un tema
    @DeleteMapping("materia/contenido/tema/{idTema}")
    public ResponseEntity<?> eliminarTema(@PathVariable Long idTema){
        return ResponseEntity.ok(temaServ.eliminarTema(idTema));
    }

    // Agregar material de apoyo
    @PostMapping("materia/contenido/material")
    public ResponseEntity<?> agregarMaterialApoyo(@RequestBody MaterialApoyoRequest request){
        return ResponseEntity.ok(matServ.crearMaterialApoyo(request));
    }

    // Editar material de apoyo
    @PutMapping("materia/contenido/material")
    public ResponseEntity<?> editarMaterialApoyo(@RequestBody MaterialApoyoRequest request){
        return ResponseEntity.ok(matServ.editarMaterialApoyo(request));
    }

    // Eliminar material de apoyo
    @DeleteMapping("materia/contenido/material/{idMaterial}")
    public ResponseEntity<?> eliminarMaterialApoyo(@PathVariable Long idMaterial){
        return ResponseEntity.ok(matServ.eliminarMaterialApoyo(idMaterial));
    }

    // Traer material de apoyo por tema
    @GetMapping("materia/contenido/material/{idTema}")
    public ResponseEntity<?> obtenerMaterialApoyoPorTema(@PathVariable Long idTema){
        return ResponseEntity.ok(matServ.obtenerPorTema(idTema));
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
