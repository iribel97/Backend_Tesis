package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.request.*;
import com.tesis.BackV2.services.cicloacademico.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class CicloAcademicoController {

    private final CicloServ cicloServ;
    private final GradoServ gradoServ;
    private final AulaServ aulaServ;
    private final MateriaServ materiaServ;
    private final DistributivoServ distributivoServ;
    private final HorarioServ horarioServ;

    /* -------------------- CICLO ACADEMICO -------------------- */




    /* -------------------- CURSOS/AULAS ACADEMICAS -------------------- */
    // Crear
    @PostMapping("curso")
    public ResponseEntity<ApiResponse<?>> crearAula(@RequestBody CursoRequest request) {
        return ResponseEntity.ok(aulaServ.crearAula(request));
    }

    // Traer todos
    @GetMapping("curso")
    public ResponseEntity<?> obtenerAulas() {
        return ResponseEntity.ok(aulaServ.obtenerAulas());
    }

    // Traer por paralelo y nombre del grado
    @GetMapping("curso/{nombre}/{paralelo}")
    public ResponseEntity<?> obtenerAula(@PathVariable String nombre, @PathVariable String paralelo) {
        return ResponseEntity.ok(aulaServ.obtenerAula(paralelo, nombre));
    }

    // Actualizar
    @PutMapping("curso")
    public ResponseEntity<ApiResponse<?>> actualizarAula(@RequestBody CursoRequest request) {
        return ResponseEntity.ok(aulaServ.editarAula(request));
    }

    // Eliminar
    @DeleteMapping("curso/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarAula(@PathVariable Long id) {
        return ResponseEntity.ok(aulaServ.eliminarAula(id));
    }





    /* -------------------- HORARIO -------------------- */
    // Crear
    @PostMapping("horario")
    public ResponseEntity<ApiResponse<?>> crearHorario(@RequestBody HorarioRequest request) {
        return ResponseEntity.ok(horarioServ.crearHorario(request));
    }
}
