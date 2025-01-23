package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.dto.CursoDocenteTutorDTO;
import com.tesis.BackV2.dto.contenido.DisNotasEst;
import com.tesis.BackV2.entities.Curso;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.repositories.DocenteRepo;
import com.tesis.BackV2.request.AsistenciaRequest;
import com.tesis.BackV2.request.CitacionRequest;
import com.tesis.BackV2.request.asistencia.AsisRequest;
import com.tesis.BackV2.request.contenido.*;
import com.tesis.BackV2.services.AsistenciaServ;
import com.tesis.BackV2.services.CicloAcademicoServ;
import com.tesis.BackV2.services.CitacionService;
import com.tesis.BackV2.services.ContenidoServ;
import com.tesis.BackV2.services.cicloacademico.SisCalifServ;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/docente/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class DocenteController {

    private final JwtService jwtService;

    private final CicloAcademicoServ cicloAServ;
    private final ContenidoServ contServ;
    private final SisCalifServ sisCalifServ;
    private final AsistenciaServ asistenciaServ;
    private final CitacionService citacionServ;

    private final DocenteRepo repDocente;

    // % asistencias par ael admin
    @GetMapping("admin/dashboard/total/asistencias")
    public ResponseEntity<?> obtenerAsistencias(HttpServletRequest request) {
        return ResponseEntity.ok(asistenciaServ.cantAsisTotalCiclo());
    }

    // mostrar notas por curso tutor
    @GetMapping("tutor/notas")
    public ResponseEntity<?> notasCursoTutor(HttpServletRequest request){
        // Traer docente
        Docente docente = validarDocente(request);
        if (docente == null) return buildErrorResponse("Docente no encontrado", 400);
        Curso curso = cicloAServ.cursoTutor(docente.getId());

        if (curso == null) return null;

        List<DisNotasEst> peoresNotas = contServ.notasCurso(curso.getId());

        return ResponseEntity.ok(CursoDocenteTutorDTO.builder()
                        .curso(curso.getGrado().getNombre() + " " + curso.getParalelo())
                        .asistenciasGeneral(asistenciaServ.asistenciasByCurso(curso.getId()))
                        .asistenciaByMateria(asistenciaServ.asisPorDisDeUnCurso(curso.getId()))
                        .peorProm(peoresNotas.getFirst())
                        .estMasInasistencias(asistenciaServ.estudianteConMasFaltas(curso.getId()))
                        .notasEstudiantes(peoresNotas)
                .build());
    }

    // mostrar si tiene horario el día de hoy
    @GetMapping("dashboard/horario/hoy")
    public ResponseEntity<?> horarioHoy(HttpServletRequest request){
        Docente docente = validarDocente(request);
        if (docente == null) return buildErrorResponse("Docente no encontrado", 400);
        return ResponseEntity.ok(cicloAServ.getHorarioDeHoy(docente.getId()));
    }

    // traer asignaciones que falten por calificar
    @GetMapping("dashboard/asignaciones/pendientes")
    public ResponseEntity<?> asignacionesPendientes(HttpServletRequest request){
        Docente docente = validarDocente(request);
        if (docente == null) return buildErrorResponse("Docente no encontrado", 400);
        return ResponseEntity.ok(contServ.traerAsignacionesPendientesPorDocente(docente.getId()));
    }

    // traer promedios de los distributivos que imparte el docente
    @GetMapping("dashboard/promedios")
    public ResponseEntity<?> promediosDocente(HttpServletRequest request){
        Docente docente = validarDocente(request);
        if (docente == null) return buildErrorResponse("Docente no encontrado", 400);
        return ResponseEntity.ok(contServ.notasCursoDistributivo(docente.getId()));
    }

    /*  ---------------------------- Gestión de Asistencia  ---------------------------- */
    // Crear asistencia
    @PostMapping("asistencia")
    public ResponseEntity<?> registrarAsistencia(@RequestBody List<AsistenciaRequest> request) {
        return ResponseEntity.ok(asistenciaServ.registrarAsistencia(request));
    }

    // Traer asistencias por distributivo y fecha
    @GetMapping("asistencias/{idDist}/{fecha}")
    public ResponseEntity<?> obtenerAsistencias(@PathVariable Long idDist, @PathVariable String fecha) {
        return ResponseEntity.ok(asistenciaServ.asistenciasByDistributivoFecha(idDist, LocalDate.parse(fecha)));
    }

    // actualizar asistencia
    @PutMapping("asistencia")
    public ResponseEntity<?> actualizarAsistencia(@RequestBody List<AsistenciaRequest> requests){
        return ResponseEntity.ok(asistenciaServ.actualizarAsistencia(requests));
    }

    /*  ---------------------------- Gestión de Conducta  ---------------------------- */

    /*  ---------------------------- Gestión de Citaciones  ---------------------------- */
    // Crear citación
    @PostMapping("citacion")
    public ResponseEntity<?> crearCitacion(@RequestBody CitacionRequest request, HttpServletRequest req) {
        Docente docente = validarDocente(req);
        if (docente == null) return buildErrorResponse("Docente no encontrado", 400);
        return ResponseEntity.ok(citacionServ.crearCitacion(request, docente.getUsuario().getCedula()));
    }

    // cambiar el estado de la citacion
    @PutMapping("citacion/estado/{idCitacion}")
    public ResponseEntity<?> cambiarEstadoCitacion(@PathVariable Long idCitacion){
        return ResponseEntity.ok(citacionServ.cambiarEstadoCitacion(idCitacion));
    }

    // Mostrar citaciones por docente
    @GetMapping("citaciones")
    public ResponseEntity<?> mostrarCitaciones(HttpServletRequest request){
        Docente docente = validarDocente(request);
        if (docente == null) return buildErrorResponse("Docente no encontrado", 400);
        return ResponseEntity.ok(citacionServ.citacionByDocente(docente.getId()));
    }

    // visualizar cursos del docente
    @GetMapping("cursos")
    public ResponseEntity<?> cursosDocente(HttpServletRequest request){
        Docente docente = validarDocente(request);
        if (docente == null) return buildErrorResponse("Docente no encontrado", 400);
        return ResponseEntity.ok(cicloAServ.obtenerCursosPorDocente(docente.getId()));
    }



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

    // Eliminar unidad
    @DeleteMapping("materia/contenido/unidad/{idUnidad}")
    public ResponseEntity<?> eliminarUnidad(@PathVariable Long idUnidad){
        return ResponseEntity.ok(contServ.eliminarUnidad(idUnidad));
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

    // visualizar entregas
    @GetMapping("materia/entregas/{idAsignacion}")
    public ResponseEntity<?> visualizarEntregas(@PathVariable Long idAsignacion){
        return ResponseEntity.ok(contServ.visualizarEntregasEst(idAsignacion));
    }

    // calificar entrega
    @PutMapping("/entrega/calificar")
    public ResponseEntity<?> calificarEntrega(@RequestBody NotaRequest nota){
        return ResponseEntity.ok(contServ.calificarEntrega(nota));
    }

    /* ---------------------------------- HORARIO ----------------------------------------*/
    // Traer horario por docente
    @GetMapping("horario")
    public ResponseEntity<?> obtenerHorario(HttpServletRequest request) {
        Docente docente = validarDocente(request);
        if (docente == null) return buildErrorResponse("Docente no encontrado", 400);
        return ResponseEntity.ok(cicloAServ.getHorariosByDocente(docente.getUsuario().getCedula()));
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
