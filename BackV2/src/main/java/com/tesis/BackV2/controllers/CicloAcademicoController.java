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

    private final MateriaServ materiaServ;
    private final DistributivoServ distributivoServ;
    private final HorarioServ horarioServ;

    /* -------------------- CICLO ACADEMICO -------------------- */




    /* -------------------- CURSOS/AULAS ACADEMICAS -------------------- */






    /* -------------------- HORARIO -------------------- */
    // Crear
    @PostMapping("horario")
    public ResponseEntity<ApiResponse<?>> crearHorario(@RequestBody HorarioRequest request) {
        return ResponseEntity.ok(horarioServ.crearHorario(request));
    }
}
