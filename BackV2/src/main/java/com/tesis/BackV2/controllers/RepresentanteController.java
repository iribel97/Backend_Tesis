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
    public ResponseEntity<ApiResponse<?>> crearInscripcion(@ModelAttribute InscripcionRequest request,
                                                           @RequestParam("cedulaEstudiante") MultipartFile cedulaEstudiante,
                                                           @RequestParam("cedulaPadre")MultipartFile cedulaPadre,
                                                           @RequestParam("cedulaMadre")MultipartFile cedulaMadre,
                                                           @RequestParam("certificadoNotas")MultipartFile certificadoNotas,
                                                           @RequestParam("serviciosBasicos")MultipartFile serviciosBasicos) throws IOException {
        return ResponseEntity.ok(inscripServ.inscripcion(request, cedulaEstudiante, cedulaPadre, cedulaMadre, certificadoNotas, serviciosBasicos));
    }

    // Editar
    @PutMapping("inscripcion/estudiante")
    public ResponseEntity<ApiResponse<?>> editarInscripcion(@ModelAttribute InscripcionRequest request,
                                                            @RequestParam("cedulaEstudiante") MultipartFile cedulaEstudiante,
                                                            @RequestParam("cedulaPadre")MultipartFile cedulaPadre,
                                                            @RequestParam("cedulaMadre")MultipartFile cedulaMadre,
                                                            @RequestParam("certificadoNotas")MultipartFile certificadoNotas,
                                                            @RequestParam("serviciosBasicos")MultipartFile serviciosBasicos) throws IOException {
        return ResponseEntity.ok(inscripServ.editarInscripcion(request, cedulaEstudiante, cedulaPadre, cedulaMadre, certificadoNotas, serviciosBasicos));
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

    /*  ---------------------------- Visualización de Horarios  ---------------------------- */

    /*  ---------------------------- Visualización de Cursos  ---------------------------- */

}
