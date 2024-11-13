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

    /* -------------------- MATERIAS ACADEMICO -------------------- */
    // Crear una materia
    @PostMapping("materia")
    public ResponseEntity<String> crearMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(service.crearMateria(request));
    }

    // Traer todas las materias
    @GetMapping("materia")
    public ResponseEntity<?> getMaterias() {
        return ResponseEntity.ok(service.getMaterias());
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
}
