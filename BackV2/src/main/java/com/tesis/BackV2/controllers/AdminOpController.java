package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.request.CursoRequest;
import com.tesis.BackV2.request.HorarioRequest;
import com.tesis.BackV2.services.cicloacademico.CursoServ;
import com.tesis.BackV2.services.cicloacademico.HorarioServ;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adminop/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class AdminOpController {

    private final CursoServ cursoServ;
    private final HorarioServ horarioServ;

    /*  ---------------------------- Gestión de Matricula  ---------------------------- */

    /*  ---------------------------- Gestión de Inscripcion  ---------------------------- */

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
