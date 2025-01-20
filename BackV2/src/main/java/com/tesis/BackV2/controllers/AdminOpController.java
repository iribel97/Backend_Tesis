package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.enums.*;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.request.CursoRequest;
import com.tesis.BackV2.request.HorarioRequest;
import com.tesis.BackV2.request.MatriculacionRequest;
import com.tesis.BackV2.services.CicloAcademicoServ;
import com.tesis.BackV2.services.UsuarioServ;
import com.tesis.BackV2.services.config.HorarioConfigServ;
import com.tesis.BackV2.services.inscripcion.InscripcionService;
import com.tesis.BackV2.services.inscripcion.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adminop/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://192.168.2.149:4200"})
public class AdminOpController {

    private final CicloAcademicoServ cicloAServ;
    private final InscripcionService inscripServ;
    private final HorarioConfigServ horarioConfigServ;
    private final UsuarioServ usuarioServ;
    private final AuthService authService;
    private final MatriculaService matriculaService;

    // Traer docentes
    @GetMapping("docentes")
    public ResponseEntity<?> getDocentes() {
        return ResponseEntity.ok(usuarioServ.getDocentes());
    }

    /*--------------------------- DASHBOARD --------------------------------------------------*/
    // Traer cantidad de inscripciones
    @GetMapping("dashboard/inscripciones")
    public ResponseEntity<?> cantidadesInscripciones() {
        return ResponseEntity.ok(inscripServ.inscripcionesPorEstadoYCiclo());
    }

    // Traer cantidad de matriculas
    @GetMapping("dashboard/matriculas")
    public ResponseEntity<?> cantidadesMatriculas() {
        return ResponseEntity.ok(matriculaService.matriculasCanntEstCiclo());
    }

    // Traer la cantidad de estudiantes por aulas
    @GetMapping("dashboard/estudiantes/aulas")
    public ResponseEntity<?> cantEstPorAulas() {
        return ResponseEntity.ok(cicloAServ.obtenerCantidadesEstudiantesPorAula());
    }

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

    // Listar una sola inscripción
    @GetMapping("inscripcion/{cedula}")
    public ResponseEntity<?> obtenerInscripcion(@PathVariable String cedula) {
        return ResponseEntity.ok(inscripServ.getInscripcion(cedula));
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
    @GetMapping("distributivos/{cicloId}/curso/{cursoId}")
    public ResponseEntity<?> obtenerDistributivos(@PathVariable Long cicloId, @PathVariable Long cursoId) {
        return ResponseEntity.ok(cicloAServ.getDistributivoByCicloAndCurso(cicloId, cursoId));
    }

    // Crear
    @PostMapping("horario")
    public ResponseEntity<ApiResponse<?>> crearHorario(@RequestBody HorarioRequest request) {
        return ResponseEntity.ok(cicloAServ.crearHorario(request));
    }

    // Editar
    @PutMapping("horario")
    public ResponseEntity<ApiResponse<?>> editarHorario(@RequestBody HorarioRequest request) {
        return ResponseEntity.ok(cicloAServ.editarHorario(request));
    }

    // Eliminar
    @DeleteMapping("horario/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarHorario(@PathVariable Long id) {
        return ResponseEntity.ok(cicloAServ.eliminarHorario(id));
    }

    /*  ---------------------------- Gestión de Cursos  ---------------------------- */

    // Crear
    @PostMapping("curso")
    public ResponseEntity<ApiResponse<?>> crearAula(@RequestBody CursoRequest request) {
        return ResponseEntity.ok(cicloAServ.crearCurso(request));
    }

    // Traer todos
    @GetMapping("cursos")
    public ResponseEntity<?> obtenerAulas() {
        return ResponseEntity.ok(cicloAServ.obtenerAulas());
    }

    // Actualizar
    @PutMapping("curso")
    public ResponseEntity<ApiResponse<?>> actualizarAula(@RequestBody CursoRequest request) {
        return ResponseEntity.ok(cicloAServ.editarAula(request));
    }

    // Eliminar
    @DeleteMapping("curso/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarAula(@PathVariable Long id) {
        return ResponseEntity.ok(cicloAServ.eliminarAula(id));
    }

    /*  ---------------------------- Visualización Estudiantes Matriculados  ---------------------------- */

    // Estudiantes matriculados
    @GetMapping("estudiantes/matriculados")
    public ResponseEntity<?> estMatriculados() throws MiExcepcion {
        return ResponseEntity.ok(usuarioServ.estMatriculadosCicloAct());
    }
}
