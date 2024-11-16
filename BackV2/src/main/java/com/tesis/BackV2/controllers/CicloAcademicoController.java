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
    // Crear un ciclo académico
    @PostMapping("ciclo")
    public ResponseEntity<String> crearCicloAcademico(@RequestBody CicloARequest request) {
        return ResponseEntity.ok(service.crearCicloAcademico(request));
    }

    // Traer todos los ciclos académicos
    @GetMapping("ciclo")
    public ResponseEntity<?> getCiclosAcademicos() {
        return ResponseEntity.ok(service.getCiclos());
    }

    // Traer un solo ciclo académico por id
    @GetMapping("ciclo/{id}")
    public ResponseEntity<?> getCicloAcademico(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCiclo(id));
    }

    //Actualizar un ciclo académico
    @PutMapping("ciclo")
    public ResponseEntity<String> actualizarCicloAcademico(@RequestBody CicloARequest request) {
        return ResponseEntity.ok(service.editarCiclo(request));
    }

    /* -------------------- GRADO ACADEMICO -------------------- */
    // Crear un grado
    @PostMapping("grado")
    public ResponseEntity<String> crearGrado(@RequestBody Grado request) {
        return ResponseEntity.ok(service.crearGrado(request));
    }

    // Traer todos los grados
    @GetMapping("grado")
    public ResponseEntity<?> getGrados() {
        return ResponseEntity.ok(service.getGrados());
    }

    // Traer grado por nombre
    @GetMapping("grado/{nombre}")
    public ResponseEntity<?> getGrado(@PathVariable String nombre) {
        return ResponseEntity.ok(service.getGrado(nombre));
    }

    // Actualizar un grado
    @PutMapping("grado")
    public ResponseEntity<String> actualizarGrado(@RequestBody Grado request) {
        return ResponseEntity.ok(service.editarGrado(request));
    }

    /* -------------------- CURSOS/AULAS ACADEMICAS -------------------- */
    // Crear un aula
    @PostMapping("curso")
    public ResponseEntity<String> crearAula(@RequestBody AulaRequest request) {
        return ResponseEntity.ok(service.crearAula(request));
    }

    // Traer todos los cursos/aulas
    @GetMapping("curso")
    public ResponseEntity<?> getAulas() {
        return ResponseEntity.ok(service.getAulas());
    }

    // Traer aula por paralelo y nombre del grado
    @GetMapping("curso/{nombre}/{paralelo}")
    public ResponseEntity<?> getAula(@PathVariable String nombre, @PathVariable String paralelo) {
        return ResponseEntity.ok(service.getAula(paralelo, nombre));
    }

    // Actualizar curso/aula
    @PutMapping("curso")
    public ResponseEntity<String> actualizarAula(@RequestBody AulaRequest request) {
        return ResponseEntity.ok(service.editarAula(request));
    }

    /* -------------------- MATERIAS ACADEMICO -------------------- */
    // Crear una materia
    @PostMapping("materia")
    public ResponseEntity<String> crearMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(service.crearMateria(request));
    }

    // Traer todas las materias
    @GetMapping("materias")
    public ResponseEntity<?> getMaterias() {
        return ResponseEntity.ok(service.getMaterias());
    }

    // Traer materia por id
    @GetMapping("materia/{id}")
    public ResponseEntity<?> getMateria(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMateria(id));
    }

    // Actualizar materia
    @PutMapping("materia")
    public ResponseEntity<String> actualizarMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(service.editarMateria(request));
    }

    /* -------------------- DISTRIBUTIVO -------------------- */
    // Crear un distributivo
    @PostMapping("distributivo")
    public ResponseEntity<String> crearDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(service.crearDistributivo(request));
    }

    // Traer todos los distributivos
    @GetMapping("distributivo")
    public ResponseEntity<?> getDistributivos() {
        return ResponseEntity.ok(service.getDistributivos());
    }

    // Traer distributivo por id
    @GetMapping("distributivo/{id}")
    public ResponseEntity<?> getDistributivo(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDistributivo(id));
    }

    // Traer distributivo por id del ciclo académico
    @GetMapping("distributivo/ciclo/{id}")
    public ResponseEntity<?> getDistributivoByCiclo(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDistributivoByCiclo(id));
    }

    // Actualizar distributivo
    @PutMapping("distributivo")
    public ResponseEntity<String> actualizarDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(service.editarDistributivo(request));
    }
}
