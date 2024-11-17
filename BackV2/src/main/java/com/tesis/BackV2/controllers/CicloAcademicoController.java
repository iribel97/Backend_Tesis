package com.tesis.BackV2.controllers;

import com.tesis.BackV2.entities.Grado;
import com.tesis.BackV2.request.AulaRequest;
import com.tesis.BackV2.request.CicloARequest;
import com.tesis.BackV2.request.DistributivoRequest;
import com.tesis.BackV2.request.MateriaRequest;
import com.tesis.BackV2.services.CicloAcademicoServ;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class CicloAcademicoController {

    private final CicloAcademicoServ service;

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
        return ResponseEntity.ok(service.crearGrado(request));
    }

    // Traer todos
    @GetMapping("grado")
    public ResponseEntity<?> getGrados() {
        return ResponseEntity.ok(service.getGrados());
    }

    // Traer grado por nombre
    @GetMapping("grado/{nombre}")
    public ResponseEntity<?> getGrado(@PathVariable String nombre) {
        return ResponseEntity.ok(service.getGrado(nombre));
    }

    // Actualizar
    @PutMapping("grado")
    public ResponseEntity<String> actualizarGrado(@RequestBody Grado request) {
        return ResponseEntity.ok(service.editarGrado(request));
    }

    // Eliminar
    @DeleteMapping("grado/{id}")
    public ResponseEntity<String> eliminarGrado(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminarGrado(id));
    }

    /* -------------------- CURSOS/AULAS ACADEMICAS -------------------- */
    // Crear
    @PostMapping("curso")
    public ResponseEntity<String> crearAula(@RequestBody AulaRequest request) {
        return ResponseEntity.ok(service.crearAula(request));
    }

    // Traer todos
    @GetMapping("curso")
    public ResponseEntity<?> getAulas() {
        return ResponseEntity.ok(service.getAulas());
    }

    // Traer por paralelo y nombre del grado
    @GetMapping("curso/{nombre}/{paralelo}")
    public ResponseEntity<?> getAula(@PathVariable String nombre, @PathVariable String paralelo) {
        return ResponseEntity.ok(service.getAula(paralelo, nombre));
    }

    // Actualizar
    @PutMapping("curso")
    public ResponseEntity<String> actualizarAula(@RequestBody AulaRequest request) {
        return ResponseEntity.ok(service.editarAula(request));
    }

    // Eliminar
    @DeleteMapping("curso/{id}")
    public ResponseEntity<String> eliminarAula(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminarAula(id));
    }

    /* -------------------- MATERIAS ACADEMICO -------------------- */
    // Crear
    @PostMapping("materia")
    public ResponseEntity<String> crearMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(service.crearMateria(request));
    }

    // Traer todas
    @GetMapping("materias")
    public ResponseEntity<?> getMaterias() {
        return ResponseEntity.ok(service.getMaterias());
    }

    // Traer por id
    @GetMapping("materia/{id}")
    public ResponseEntity<?> getMateria(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMateria(id));
    }

    // Actualizar
    @PutMapping("materia")
    public ResponseEntity<String> actualizarMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(service.editarMateria(request));
    }

    // Eliminar
    @DeleteMapping("materia/{id}")
    public ResponseEntity<String> eliminarMateria(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminarMateria(id));
    }

    /* -------------------- DISTRIBUTIVO -------------------- */
    // Crear
    @PostMapping("distributivo")
    public ResponseEntity<String> crearDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(service.crearDistributivo(request));
    }

    // Traer todos
    @GetMapping("distributivo")
    public ResponseEntity<?> getDistributivos() {
        return ResponseEntity.ok(service.getDistributivos());
    }

    // Traer por id
    @GetMapping("distributivo/{id}")
    public ResponseEntity<?> getDistributivo(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDistributivo(id));
    }

    // Traer por id del ciclo académico
    @GetMapping("distributivo/ciclo/{id}")
    public ResponseEntity<?> getDistributivoByCiclo(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDistributivoByCiclo(id));
    }

    // Actualizar
    @PutMapping("distributivo")
    public ResponseEntity<String> actualizarDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(service.editarDistributivo(request));
    }

    // Eliminar
    @DeleteMapping("distributivo/{id}")
    public ResponseEntity<String> eliminarDistributivo(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminarDistributivo(id));
    }
}
