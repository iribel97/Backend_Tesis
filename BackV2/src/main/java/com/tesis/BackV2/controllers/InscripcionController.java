package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.enums.EstadoInscripcion;
import com.tesis.BackV2.request.InscripcionRequest;
import com.tesis.BackV2.services.inscripcion.InscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/inscripcion/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class InscripcionController {

    private final InscripcionService serv;

    /* -------------------- INSCRIPCION -------------------- */
    // Crear
    @PostMapping("estudiante")
    public ResponseEntity<ApiResponse<?>> crearInscripcion(@ModelAttribute InscripcionRequest request,
                                                           @RequestParam("cedulaEstudiante")MultipartFile cedulaEstudiante,
                                                           @RequestParam("cedulaPadre")MultipartFile cedulaPadre,
                                                           @RequestParam("cedulaMadre")MultipartFile cedulaMadre,
                                                           @RequestParam("certificadoNotas")MultipartFile certificadoNotas,
                                                           @RequestParam("serviciosBasicos")MultipartFile serviciosBasicos) throws IOException {
        return ResponseEntity.ok(serv.inscripcion(request, cedulaEstudiante, cedulaPadre, cedulaMadre, certificadoNotas, serviciosBasicos));
    }

    // aceptar inscripción
    @PutMapping("adminop/aceptar")
    public ResponseEntity<ApiResponse<?>> aceptarInscripcion(@RequestParam String cedula) {
        return ResponseEntity.ok(serv.cambiarEstadoInscripcion(cedula, EstadoInscripcion.Aceptado));
    }

    // rechazar inscripción
    @PutMapping("adminop/rechazar")
    public ResponseEntity<ApiResponse<?>> rechazarInscripcion(@RequestParam String cedula) {
        return ResponseEntity.ok(serv.cambiarEstadoInscripcion(cedula, EstadoInscripcion.Rechazado));
    }
}
