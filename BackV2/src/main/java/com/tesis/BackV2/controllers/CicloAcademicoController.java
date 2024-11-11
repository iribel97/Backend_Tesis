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

    // Crear un ciclo académico
    @PostMapping("ciclo")
    public ResponseEntity<String> crearCicloAcademico(@RequestBody CicloARequest request) {
        return ResponseEntity.ok(service.crearCicloAcademico(request));
    }

    // Crear un grado
    @PostMapping("grado")
    public ResponseEntity<String> crearGrado(@RequestBody Grado request) {
        return ResponseEntity.ok(service.crearGrado(request));
    }

    // Crear un aula
    @PostMapping("aula")
    public ResponseEntity<String> crearAula(@RequestBody AulaRequest request) {
        return ResponseEntity.ok(service.crearAula(request));
    }

    // Crear una materia
    @PostMapping("materia")
    public ResponseEntity<String> crearMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(service.crearMateria(request));
    }

    // Crear un distributivo
    @PostMapping("distributivo")
    public ResponseEntity<String> crearDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(service.crearDistributivo(request));
    }
}
