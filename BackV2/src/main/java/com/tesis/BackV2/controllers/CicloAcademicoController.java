package com.tesis.BackV2.controllers;

import com.tesis.BackV2.entities.Grado;
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

    private final CicloServ service;
    private final GradoServ gradoServ;
    private final AulaServ aulaServ;
    private final MateriaServ materiaServ;
    private final DistributivoServ distributivoServ;
    private final HorarioServ horarioServ;

    /* -------------------- CICLO ACADEMICO -------------------- */
    // Crear
    @PostMapping("ciclo")
    public ResponseEntity<String> crearCicloAcademico(@RequestBody CicloARequest request) {
        return ResponseEntity.ok(service.crearCicloAcademico(request));
    }

    // Traer todos
    @GetMapping("ciclo")
    public ResponseEntity<?> getCiclosAcademicos() {
        return ResponseEntity.ok(service.getCiclos());
    }

    // Traer un solo por id
    @GetMapping("ciclo/{id}")
    public ResponseEntity<?> getCicloAcademico(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCiclo(id));
    }

    //Actualizar
    @PutMapping("ciclo")
    public ResponseEntity<String> actualizarCicloAcademico(@RequestBody CicloARequest request) {
        return ResponseEntity.ok(service.editarCiclo(request));
    }

    // Eliminar
    @DeleteMapping("ciclo/{id}")
    public ResponseEntity<String> eliminarCicloAcademico(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminarCiclo(id));
    }

    /* -------------------- GRADO ACADEMICO -------------------- */
    // Crear
    @PostMapping("grado")
    public ResponseEntity<String> crearGrado(@RequestBody Grado request) {
        return ResponseEntity.ok(gradoServ.crearGrado(request));
    }

    // Traer todos
    @GetMapping("grado")
    public ResponseEntity<?> getGrados() {
        return ResponseEntity.ok(gradoServ.getGrados());
    }

    // Traer grado por nombre
    @GetMapping("grado/{nombre}")
    public ResponseEntity<?> getGrado(@PathVariable String nombre) {
        return ResponseEntity.ok(gradoServ.getGrado(nombre));
    }

    // Actualizar
    @PutMapping("grado")
    public ResponseEntity<String> actualizarGrado(@RequestBody Grado request) {
        return ResponseEntity.ok(gradoServ.editarGrado(request));
    }

    // Eliminar
    @DeleteMapping("grado/{id}")
    public ResponseEntity<String> eliminarGrado(@PathVariable Long id) {
        return ResponseEntity.ok(gradoServ.eliminarGrado(id));
    }

    /* -------------------- CURSOS/AULAS ACADEMICAS -------------------- */
    // Crear
    @PostMapping("curso")
    public ResponseEntity<String> crearAula(@RequestBody AulaRequest request) {
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
    public ResponseEntity<String> actualizarAula(@RequestBody AulaRequest request) {
        return ResponseEntity.ok(aulaServ.editarAula(request));
    }

    // Eliminar
    @DeleteMapping("curso/{id}")
    public ResponseEntity<String> eliminarAula(@PathVariable Long id) {
        return ResponseEntity.ok(aulaServ.eliminarAula(id));
    }

    /* -------------------- MATERIAS ACADEMICO -------------------- */
    // Crear
    @PostMapping("materia")
    public ResponseEntity<String> crearMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(materiaServ.crearMateria(request));
    }

    // Traer todas
    @GetMapping("materias")
    public ResponseEntity<?> getMaterias() {
        return ResponseEntity.ok(materiaServ.getMaterias());
    }

    // Traer por id
    @GetMapping("materia/{id}")
    public ResponseEntity<?> getMateria(@PathVariable Long id) {
        return ResponseEntity.ok(materiaServ.getMateria(id));
    }

    // Actualizar
    @PutMapping("materia")
    public ResponseEntity<String> actualizarMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(materiaServ.editarMateria(request));
    }

    // Eliminar
    @DeleteMapping("materia/{id}")
    public ResponseEntity<String> eliminarMateria(@PathVariable Long id) {
        return ResponseEntity.ok(materiaServ.eliminarMateria(id));
    }

    /* -------------------- DISTRIBUTIVO -------------------- */
    // Crear
    @PostMapping("distributivo")
    public ResponseEntity<String> crearDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(distributivoServ.crearDistributivo(request));
    }

    // Traer todos
    @GetMapping("distributivo")
    public ResponseEntity<?> getDistributivos() {
        return ResponseEntity.ok(distributivoServ.obtenerDistributivos());
    }

    // Traer por id
    @GetMapping("distributivo/{id}")
    public ResponseEntity<?> getDistributivo(@PathVariable Long id) {
        return ResponseEntity.ok(distributivoServ.obtenerDistributivo(id));
    }

    // Traer por id del ciclo académico
    @GetMapping("distributivo/ciclo/{id}")
    public ResponseEntity<?> getDistributivoByCiclo(@PathVariable Long id) {
        return ResponseEntity.ok(distributivoServ.getDistributivoByCiclo(id));
    }

    // Actualizar
    @PutMapping("distributivo")
    public ResponseEntity<String> actualizarDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(distributivoServ.editarDistributivo(request));
    }

    // Eliminar
    @DeleteMapping("distributivo/{id}")
    public ResponseEntity<String> eliminarDistributivo(@PathVariable Long id) {
        return ResponseEntity.ok(distributivoServ.eliminarDistributivo(id));
    }

    /* -------------------- HORARIO -------------------- */
    // Crear
    @PostMapping("horario")
    public ResponseEntity<String> crearHorario(@RequestBody HorarioRequest request) {
        return ResponseEntity.ok(horarioServ.crearHorario(request));
    }
}
