package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.config.auth.RegisterRequest;
import com.tesis.BackV2.entities.Inscripcion;
import com.tesis.BackV2.enums.*;
import com.tesis.BackV2.repositories.InscripcionRepo;
import com.tesis.BackV2.request.CursoRequest;
import com.tesis.BackV2.request.HorarioRequest;
import com.tesis.BackV2.request.MatriculacionRequest;
import com.tesis.BackV2.services.cicloacademico.CursoServ;
import com.tesis.BackV2.services.cicloacademico.DistributivoServ;
import com.tesis.BackV2.services.cicloacademico.HorarioServ;
import com.tesis.BackV2.services.config.HorarioConfigServ;
import com.tesis.BackV2.services.inscripcion.InscripcionService;
import com.tesis.BackV2.services.inscripcion.MatriculaService;
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
    private final DistributivoServ distributivoServ;
    private final HorarioConfigServ horarioConfigServ;
    private final AuthService authService;
    private final MatriculaService matriculaService;

    /*  ---------------------------- Gestión de Matricula  ---------------------------- */
    // Listar por pendientes
    @GetMapping("matriculas/pendientes")
    public ResponseEntity<?> listarMatriculasPendientes() {
        return ResponseEntity.ok(matriculaService.listarPorEstado(EstadoMatricula.Pendiente));
    }

    // Aceptar
    @PutMapping("matricula/aceptar")
    public ResponseEntity<ApiResponse<?>> aceptarMatricula(@RequestBody MatriculacionRequest request) {
        return ResponseEntity.ok(matriculaService.cambiarEstMatricula(EstadoMatricula.Matriculado, request));
    }


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

    // aceptar prueba adicional
    @PutMapping("inscripcion/prueba/{tipoPrueba}/{cedula}")
    public ResponseEntity<ApiResponse<?>> aceptarPruebaAdicional(@PathVariable String tipoPrueba, @PathVariable String cedula) {
        TipoPrueba tipo = TipoPrueba.valueOf(tipoPrueba.toUpperCase());
        return ResponseEntity.ok(inscripServ.cambiarEstadoPruebaAdicional(cedula, tipo, EstadoInscripcion.Aceptado));
    }

    // rechazar inscripción
    @PutMapping("inscripcion/rechazar/{cedulaEst}")
    public ResponseEntity<ApiResponse<?>> rechazarInscripcion(@PathVariable String cedulaEst) {
        return ResponseEntity.ok(inscripServ.cambiarEstadoInscripcion(cedulaEst, EstadoInscripcion.Rechazado, null));
    }

    /*  ---------------------------- Gestión de Horarios  ---------------------------- */
    // Traer todos las configuraciones de horarios
    @GetMapping("horariosConfig")
    public ResponseEntity<?> obtenerHorarios() {
        return ResponseEntity.ok(horarioConfigServ.horarios());
    }

    // Traer distributivos por ciclo academico y curso
    @GetMapping("distributivos/{cicloId}/{cursoId}")
    public ResponseEntity<?> obtenerDistributivos(@PathVariable Long cicloId, @PathVariable Long cursoId) {
        return ResponseEntity.ok(distributivoServ.getDistributivoByCicloAndCurso(cicloId, cursoId));
    }

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

    /*  ---------------------------- Gestión de Cursos  ---------------------------- */

    // Crear
    @PostMapping("curso")
    public ResponseEntity<ApiResponse<?>> crearAula(@RequestBody CursoRequest request) {
        return ResponseEntity.ok(cursoServ.crearCurso(request));
    }

    // Traer todos
    @GetMapping("cursos")
    public ResponseEntity<?> obtenerAulas() {
        return ResponseEntity.ok(cursoServ.obtenerAulas());
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
