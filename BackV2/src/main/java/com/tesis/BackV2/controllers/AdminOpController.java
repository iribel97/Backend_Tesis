package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.config.auth.RegisterRequest;
import com.tesis.BackV2.entities.Inscripcion;
import com.tesis.BackV2.enums.EstadoInscripcion;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.enums.TipoPrueba;
import com.tesis.BackV2.repositories.InscripcionRepo;
import com.tesis.BackV2.request.CursoRequest;
import com.tesis.BackV2.request.HorarioRequest;
import com.tesis.BackV2.request.MatriculacionRequest;
import com.tesis.BackV2.services.cicloacademico.CursoServ;
import com.tesis.BackV2.services.cicloacademico.HorarioServ;
import com.tesis.BackV2.services.inscripcion.InscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adminop/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class AdminOpController {

    private final InscripcionService inscripServ;
    private final CursoServ cursoServ;
    private final HorarioServ horarioServ;
    private final AuthService authService;

    /*  ---------------------------- Gestión de Matricula  ---------------------------- */

    /*  ---------------------------- Gestión de Inscripcion  ---------------------------- */
    // Listar por estado pendiente
    @GetMapping("inscripciones/pendientes")
    public ResponseEntity<?> listarInscripcionesPendientes() {
        return ResponseEntity.ok(inscripServ.getInscripcionesPendientes());
    }

    // aceptar inscripción
    @PutMapping("inscripcion/aceptar")
    public ResponseEntity<ApiResponse<?>> aceptarInscripcion(@RequestBody MatriculacionRequest request) {
        return ResponseEntity.ok(inscripServ.cambiarEstadoInscripcion(request.getCedulaEstudiante(), EstadoInscripcion.Aceptado, request.getParalelo()));
    }

    @PutMapping("inscripcion/prueba/{tipoPrueba}/{cedula}")
    public ResponseEntity<ApiResponse<?>> aceptarPruebaAdicional(@PathVariable String tipoPrueba, @PathVariable String cedula) {
        TipoPrueba tipo = TipoPrueba.valueOf(tipoPrueba.toUpperCase());
        return ResponseEntity.ok(inscripServ.cambiarEstadoPruebaAdicional(cedula, tipo, EstadoInscripcion.Aceptado));
    }

    // rechazar inscripción
    @PutMapping("inscripcion/rechazar")
    public ResponseEntity<ApiResponse<?>> rechazarInscripcion(@RequestBody MatriculacionRequest request) {
        return ResponseEntity.ok(inscripServ.cambiarEstadoInscripcion(request.getCedulaEstudiante(), EstadoInscripcion.Rechazado, null));
    }

    /*  ---------------------------- Gestión de Horarios  ---------------------------- */
    // Crear
    @PostMapping("horario")
    public ResponseEntity<ApiResponse<?>> crearHorario(@RequestBody HorarioRequest request) {
        return ResponseEntity.ok(horarioServ.crearHorario(request));
    }

    // Editar
    @PutMapping("horario")
    public ResponseEntity<ApiResponse<?>> editarHorario(@RequestBody HorarioRequest request) {
        return ResponseEntity.ok(horarioServ.editarHorario(request));
    }

    // Eliminar
    @DeleteMapping("horario/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarHorario(@PathVariable Long id) {
        return ResponseEntity.ok(horarioServ.eliminarHorario(id));
    }

    // Traer por curso
    @GetMapping("horarios/{idCurso}")
    public ResponseEntity<?> obtenerHorarios(@PathVariable Long idCurso) {
        return ResponseEntity.ok(horarioServ.getHorariosByCurso(idCurso));
    }

    /*  ---------------------------- Gestión de Cursos  ---------------------------- */

    // Crear
    @PostMapping("curso")
    public ResponseEntity<ApiResponse<?>> crearAula(@RequestBody CursoRequest request) {
        return ResponseEntity.ok(cursoServ.crearCurso(request));
    }

    // Registrar cursos
    @PostMapping("cursos")
    public ResponseEntity<ApiResponse<?>> registrarCursos(@RequestBody List<CursoRequest> request) {
        return ResponseEntity.ok(cursoServ.registrarCursos(request));
    }

    // Traer todos
    @GetMapping("cursos")
    public ResponseEntity<?> obtenerAulas() {
        return ResponseEntity.ok(cursoServ.obtenerAulas());
    }

    // Traer por paralelo y nombre del grado
    @GetMapping("curso/{nombre}/{paralelo}")
    public ResponseEntity<?> obtenerAula(@PathVariable String nombre, @PathVariable String paralelo) {
        return ResponseEntity.ok(cursoServ.obtenerAula(paralelo, nombre));
    }

    // Actualizar
    @PutMapping("curso")
    public ResponseEntity<ApiResponse<?>> actualizarAula(@RequestBody CursoRequest request) {
        return ResponseEntity.ok(cursoServ.editarAula(request));
    }

    // Eliminar
    @DeleteMapping("curso/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarAula(@PathVariable Long id) {
        return ResponseEntity.ok(cursoServ.eliminarAula(id));
    }

    /*  ---------------------------- Visualización Estudiantes Matriculados  ---------------------------- */
}
