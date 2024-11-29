package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.request.CicloARequest;
import com.tesis.BackV2.request.SisCalfRequest;
import com.tesis.BackV2.services.cicloacademico.SisCalifServ;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class SistemaCalifController {

    private final SisCalifServ serv;

    // crear el sistema de evaluación
    @PostMapping("calificacion")
    public ResponseEntity<ApiResponse<?>> crearSistemaCalificacion(@RequestBody SisCalfRequest request) {
        return ResponseEntity.ok(serv.crearSisCalif(request));
    }

}
