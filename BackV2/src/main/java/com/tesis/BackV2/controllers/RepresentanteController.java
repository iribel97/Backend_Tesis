package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.InscripcionDTO;
import com.tesis.BackV2.request.InscripcionRequest;
import com.tesis.BackV2.request.MatriculacionRequest;
import com.tesis.BackV2.services.inscripcion.InscripcionService;
import com.tesis.BackV2.services.inscripcion.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/representante/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class RepresentanteController {

    private final InscripcionService inscripServ;
    private final MatriculaService matricServ;

    /*  ---------------------------- Visualización de Conducta  ---------------------------- */

    /*  ---------------------------- Visualización de Calificaciones  ---------------------------- */

    /*  ---------------------------- Gestión de Inscripciones  ---------------------------- */
    // Crear
    @PostMapping("inscripcion/estudiante")
    public ResponseEntity<ApiResponse<?>> crearInscripcion(@RequestBody InscripcionRequest request) throws IOException {
        return ResponseEntity.ok(inscripServ.inscripcion(request));
    }

    // Editar
    @PutMapping("inscripcion/estudiante")
    public ResponseEntity<ApiResponse<?>> editarInscripcion(@RequestBody InscripcionRequest request) throws IOException {
        return ResponseEntity.ok(inscripServ.editarInscripcion(request));
    }

    // Eliminar
    @DeleteMapping("inscripcion/{cedulaEst}")
    public ResponseEntity<ApiResponse<?>> eliminarInscripcion(@PathVariable String cedulaEst){
        return ResponseEntity.ok(inscripServ.eliminarInscripcion(cedulaEst));
    }

    // Listar por cedula de representante
    @GetMapping("inscripcion/{cedulaRep}")
    public ResponseEntity<List<InscripcionDTO>> listarInscripciones(@PathVariable String cedulaRep){
        return ResponseEntity.ok(inscripServ.listarPorRepresentante(cedulaRep));
    }

    /*  ---------------------------- Gestión de Matricula  ---------------------------- */
    // Crear
    @PostMapping("matricula")
    public ResponseEntity<ApiResponse<?>> crearMatricula(@RequestBody MatriculacionRequest request){
        return ResponseEntity.ok(matricServ.crearMatricula(request));
    }

    // Editar
    @PutMapping("matricula")
    public ResponseEntity<ApiResponse<?>> editarMatricula(@RequestBody MatriculacionRequest request){
        return ResponseEntity.ok(matricServ.actualizarMatricula(request));
    }

    // Eliminar
    @DeleteMapping("matricula/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarMatricula(@PathVariable Long id){
        return ResponseEntity.ok(matricServ.eliminarMatricula(id));
    }

    // Listar por representante
    @GetMapping("matriculas/{cedulaRepresentante}")
    public ResponseEntity<?> traerPorRepresentante(@PathVariable String cedulaRepresentante){
        return ResponseEntity.ok(matricServ.listarPorRepresentante(cedulaRepresentante));
    }

    /*  ---------------------------- Visualización de Horarios  ---------------------------- */

    /*  ---------------------------- Visualización de Cursos  ---------------------------- */

}
