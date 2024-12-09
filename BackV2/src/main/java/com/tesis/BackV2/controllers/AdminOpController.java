package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.config.auth.RegisterRequest;
import com.tesis.BackV2.entities.Inscripcion;
import com.tesis.BackV2.enums.EstadoInscripcion;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.repositories.InscripcionRepo;
import com.tesis.BackV2.request.CursoRequest;
import com.tesis.BackV2.request.HorarioRequest;
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
    @PutMapping("inscripcion/aceptar/{cedulaEst}")
    public ResponseEntity<ApiResponse<?>> aceptarInscripcion(@PathVariable String cedulaEst) {
        authService.registerEstudiante(cedulaEst, Rol.ESTUDIANTE, EstadoUsu.Suspendido);
        return ResponseEntity.ok(inscripServ.cambiarEstadoInscripcion(cedulaEst, EstadoInscripcion.Aceptado));
    }

    // rechazar inscripción
    @PutMapping("inscripcion/rechazar/{cedulaEst}")
    public ResponseEntity<ApiResponse<?>> rechazarInscripcion(@PathVariable String cedulaEst) {
        return ResponseEntity.ok(inscripServ.cambiarEstadoInscripcion(cedulaEst, EstadoInscripcion.Rechazado));
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
